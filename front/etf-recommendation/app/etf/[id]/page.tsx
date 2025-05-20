'use client';

import { useParams, useRouter } from 'next/navigation';
import { useEffect, useState, useTransition } from 'react';
import { notFound } from 'next/navigation';
import { ArrowLeft, Star, StarHalf } from 'lucide-react';
import Link from 'next/link';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
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

type Holding = {
  name: string;
  weight: number;
};

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
  chartData: {
    daily: any[];
    weekly: any[];
    monthly: any[];
    yearly: any[];
  };
};

export default function ETFDetailPage() {
  const params = useParams();
  const etfId = params.id as string;
  const router = useRouter();

  const [etf, setEtf] = useState<ETF | null>(null);
  const [subscribed, setSubscribed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isPending, startTransition] = useTransition();

  // ETF 상세 데이터 로드
  useEffect(() => {
    if (!etfId) return;

    const loadEtfDetail = async () => {
      const { data, error } = await fetchEtfDetail(Number(etfId));

      if (error || !data) {
        console.error('ETF not found', error);
        notFound();
      }

      setEtf({
        id: data.etfId,
        name: data.etfName,
        ticker: data.etfCode,
        issuer: data.companyName,
        theme: '...',
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
        chartData: {
          daily: [],
          weekly: [],
          monthly: [],
          yearly: [],
        },
      });
    };

    loadEtfDetail();
  }, [etfId]);

  // 구독 상태 확인
  useEffect(() => {
    if (!etfId) return;

    const checkSubscription = async () => {
      const ids = await getSubscribedEtfIds();
      setSubscribed(ids.includes(Number(etfId)));
    };

    checkSubscription();
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
    return <div className="p-6">ETF 데이터를 불러오는 중입니다...</div>;
  }

  return (
      <div className="container mx-auto py-6 px-4">
        {/* 상단 네비게이션 */}
        <div className="mb-6">
          <Link href="/" className="flex items-center gap-1 text-slate-500 hover:text-slate-700 mb-4">
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
                {etf.ticker} | {etf.issuer}
              </p>
            </div>

            <div className="flex items-center gap-2">
              <Button
                  variant={subscribed ? 'secondary' : 'outline'}
                  size="icon"
                  onClick={handleToggleSubscribe}
                  disabled={loading}
              >
                {subscribed ? <Star className="h-4 w-4 text-yellow-500" /> : <StarHalf className="h-4 w-4" />}
              </Button>
            </div>
          </div>
        </div>

        {/* 주요 지표 카드 */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle>현재가</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-end gap-2">
                <div className="text-3xl font-bold">{etf.price.toLocaleString()}원</div>
                <div className={`text-lg ${etf.change >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                  {etf.change >= 0 ? '+' : ''}{etf.change}%
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>등락률</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-green-600">+{etf.returnRate}%</div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle>거래량</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{etf.volume.toLocaleString()}</div>
            </CardContent>
          </Card>
        </div>

        {/* 가격 차트 */}
        <div className="mb-8">
          <Tabs defaultValue="daily">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-bold">가격 차트</h2>
              {/*<TabsList>*/}
              {/*  <TabsTrigger value="daily">일간</TabsTrigger>*/}
              {/*  <TabsTrigger value="weekly">주간</TabsTrigger>*/}
              {/*  <TabsTrigger value="monthly">월간</TabsTrigger>*/}
              {/*  <TabsTrigger value="yearly">연간</TabsTrigger>*/}
              {/*</TabsList>*/}
            </div>

            {/* TradingView 대신 커스텀 Chart.js 컴포넌트 삽입 */}
            <div className="w-full h-[500px]">
              <StockHistoryChart />
            </div>
          </Tabs>
        </div>

        {/* ETF 정보 & 구성 종목 */}
        <div className="grid md:grid-cols-2 gap-6 mb-8">
          <Card>
            <CardHeader>
              <CardTitle>ETF 정보</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <p>{etf.description || '상세 정보가 없습니다.'}</p>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-slate-500">운용사</p>
                    <p className="font-medium">{etf.issuer}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-500">상장일</p>
                    <p className="font-medium">{etf.launchDate}</p>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>구성 종목</CardTitle>
            </CardHeader>
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
                      {etf.holdings.map((h, idx) => (
                          <TableRow key={idx}>
                            <TableCell>{h.name}</TableCell>
                            <TableCell className="text-right">{h.weight}%</TableCell>
                          </TableRow>
                      ))}
                    </TableBody>
                  </Table>
              ) : (
                  <div className="text-gray-500">구성 종목 데이터가 없습니다.</div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
  );
}
