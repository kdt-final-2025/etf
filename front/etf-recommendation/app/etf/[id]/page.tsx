// app/etf/[id]/page.tsx
'use client';

import { useParams, notFound } from 'next/navigation';
import { useEffect, useState, useTransition } from 'react';
import { ArrowLeft, Star, StarHalf } from 'lucide-react';
import Link from 'next/link';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs } from '@/components/ui/tabs';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';

import StockHistoryChart from '@/components/StockHistoryChart';

import {
  getSubscribedEtfIds,
  subscribeToEtf,
  unsubscribeFromEtf,
} from '@/app/etf/[id]/action';
import { fetchEtfDetail } from '@/lib/api/etf';

type Holding = { name: string; weight: number; };
type ETF = {
  id: number;
  name: string;
  ticker: string;
  issuer: string;
  theme: string;
  description: string;
  price: number;
  nav: number;
  change: number;
  returnRate: number;
  volume: number;
  launchDate: string;
  aum: number;
  expense: number;
  holdings: Holding[];
};

export default function ETFDetailPage() {
  const { id: etfId } = useParams() as { id: string };
  const [etf, setEtf] = useState<ETF | null>(null);
  const [subscribed, setSubscribed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isPending, startTransition] = useTransition();

  // 1) ETF 상세 데이터 로드
  useEffect(() => {
    if (!etfId) return;
    (async () => {
      const { data, error } = await fetchEtfDetail(Number(etfId));
      if (error || !data) return notFound();
      setEtf({
        id: data.etfId,
        name: data.etfName,
        ticker: data.etfCode,
        issuer: data.companyName,
        theme: data.theme || '',
        description: data.description || '',
        price: data.currentPrice || 0,
        nav: data.nav || 0,
        change: data.changeRate || 0,
        returnRate: data.returnRate || 0,
        volume: data.volume || 0,
        launchDate: data.listingDate,
        aum: data.aum || 0,
        expense: data.expenseRatio || 0,
        holdings: data.holdings || [],
      });
    })();
  }, [etfId]);

  // 2) 구독 상태 확인
  useEffect(() => {
    if (!etfId) return;
    (async () => {
      const ids = await getSubscribedEtfIds();
      setSubscribed(ids.includes(Number(etfId)));
    })();
  }, [etfId]);

  const handleToggleSubscribe = async () => {
    setLoading(true);
    try {
      if (subscribed) {
        await unsubscribeFromEtf(+etfId);
        setSubscribed(false);
      } else {
        await subscribeToEtf(+etfId);
        setSubscribed(true);
      }
    } catch (err: any) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (!etf) {
    return <div className="p-6 text-center">ETF 데이터를 불러오는 중입니다...</div>;
  }

  // 기존 UI는 그대로, 차트에 들어갈 심볼만 새로 생성
  const fullSymbol = `${etf.ticker}.KS`;

  return (
      <div className="container mx-auto py-6 px-4">
        {/* 상단 네비게이션 & 제목 */}
        <div className="mb-6">
          <Link
              href="/"
              className="flex items-center gap-1 text-slate-500 hover:text-slate-700 mb-4"
          >
            <ArrowLeft className="h-4 w-4" />
            <span>홈으로 돌아가기</span>
          </Link>
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-3xl font-bold">{etf.name}</h1>
                <Badge variant="outline">{etf.theme}</Badge>
              </div>
              <p className="text-slate-500">
                {fullSymbol} | {etf.issuer}
              </p>
            </div>
            <Button
                variant={subscribed ? 'secondary' : 'outline'}
                size="icon"
                onClick={handleToggleSubscribe}
                disabled={loading || isPending}
            >
              {subscribed
                  ? <Star className="h-4 w-4 text-yellow-500" />
                  : <StarHalf className="h-4 w-4" />}
            </Button>
          </div>
        </div>

        {/* 주요 지표 카드 */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="pb-2"><CardTitle>현재가</CardTitle></CardHeader>
            <CardContent className="flex items-end gap-2">
            <span className="text-3xl font-bold">
              {etf.price.toLocaleString()}원
            </span>
              <span className={etf.change >= 0 ? 'text-green-600' : 'text-red-600'}>
              {etf.change >= 0 ? '+' : ''}{etf.change}%
            </span>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2"><CardTitle>등락률</CardTitle></CardHeader>
            <CardContent>
            <span className="text-3xl font-bold text-green-600">
              +{etf.returnRate}%
            </span>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2"><CardTitle>거래량</CardTitle></CardHeader>
            <CardContent>
            <span className="text-3xl font-bold">
              {etf.volume.toLocaleString()}
            </span>
            </CardContent>
          </Card>
        </div>

        {/* 가격 차트 (range 버튼 포함) */}
        <div className="mb-8">
          <Tabs defaultValue="daily">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">가격 차트</h2>
            </div>
            <div className="w-full h-[500px]">
              {/* 여기만 initialSymbol 로 교체 */}
              <StockHistoryChart initialSymbol={fullSymbol} />
            </div>
          </Tabs>
        </div>

        {/* ETF 정보 & 구성 종목 */}
        <div className="grid md:grid-cols-2 gap-6 mb-8">
          <Card>
            <CardHeader><CardTitle>ETF 정보</CardTitle></CardHeader>
            <CardContent>
              <p>{etf.description || '상세 정보가 없습니다.'}</p>
              <div className="grid grid-cols-2 gap-4 mt-4">
                <div>
                  <p className="text-sm text-slate-500">운용사</p>
                  <p className="font-medium">{etf.issuer}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">상장일</p>
                  <p className="font-medium">{etf.launchDate}</p>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader><CardTitle>구성 종목</CardTitle></CardHeader>
            <CardContent>
              {etf.holdings.length > 0 ? (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>종목명</TableHead>
                        <TableHead className="text-right">비중</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {etf.holdings.map((h, i) => (
                          <TableRow key={i}>
                            <TableCell>{h.name}</TableCell>
                            <TableCell className="text-right">{h.weight}%</TableCell>
                          </TableRow>
                      ))}
                    </TableBody>
                  </Table>
              ) : (
                  <p className="text-gray-500">구성 종목 데이터가 없습니다.</p>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
  );
}
