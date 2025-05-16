"use client"
import {useEffect, useMemo, useState} from 'react'
import Link from "next/link"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card"
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Tabs, TabsContent, TabsList, TabsTrigger} from "@/components/ui/tabs"
import {Search, TrendingUp, BarChart3, ArrowUpRight, ArrowDownRight, Filter} from "lucide-react"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import EtfCard from "@/components/EtfCard";
import MarketTickerWidget from "@/components/MarketTickerWidget";
import Pagination from "@/components/StockPagination";
import StocksTable from "@/components/StocksTable";

// 샘플 ETF 데이터

type ETF = {
    id: number
    name: string
    ticker: string
    theme: string
    price: number
    change: number
    volume: number
    returnRate: number
}
// 시장 요약 데이터
const marketSummary = {
    kospi: {value: 2850.12, change: 1.2},
    kosdaq: {value: 920.45, change: -0.5},
    nasdaq: {value: 16250.8, change: 0.8},
    sp500: {value: 5120.35, change: 0.6},
}
const themeNameMap: Record<string, string> = {
    'AI_DATA': 'AI 데이터',
    'USA': '미국',
    'KOREA': '한국',
    'REITS': '리츠',
    'MULTI_ASSET': '멀티에셋',
    'COMMODITIES': '원자재',
    'HIGH_RISK': '고위험',
    'SECTOR': '섹터',
    'DIVIDEND': '배당',
    'ESG': 'ESG',
    'GOLD': '금',
    'GOVERNMENT_BOND': '국채',
    'CORPORATE_BOND': '회사채',
    'DEFENSE': '방위산업',
    'SEMICONDUCTOR': '반도체',
    'BIO': '바이오',
    'EMERGING_MARKETS': '신흥시장'
};


export default function Home() {
    const [etfData, setEtfData] = useState<ETF[]>([])
    const [searchQuery, setSearchQuery] = useState('');
    const [selectedTheme, setSelectedTheme] = useState('all');
    const [sortKey, setSortKey] = useState('returnRate');
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);

    //웹소켓
    //서버에서 총 종목 수 받기
    const [websocketPage, setWebsocketPage] = useState(0);
    const size = 10; // 한 페이지당 10개


    useEffect(() => {
        const fetchEtfs = async (page: number) => {
            try {
                const response = await fetch(`https://localhost:8443/api/v1/etfs?page=${page}&size=20&period=weekly`);
                const data = await response.json();

                const newEtfs: ETF[] = data.etfReadResponseList.map((etf: any, index: number) => ({
                    id: etf.etfId,
                    name: etf.etfName,
                    ticker: etf.etfCode,
                    theme: etf.theme,
                    price: 10000 + index * 100,
                    change: parseFloat((Math.random() * 5).toFixed(2)) * (Math.random() > 0.5 ? 1 : -1),
                    volume: Math.floor(Math.random() * 100000),
                    returnRate: etf.returnRate,
                }));

                // 중복 제거
                setEtfData((prev) => {
                    const existingIds = new Set(prev.map((etf) => etf.id));
                    const filteredNew = newEtfs.filter((etf) => !existingIds.has(etf.id));
                    return [...prev, ...filteredNew];
                });

            } catch (error) {
                console.error('ETF 데이터 로딩 에러:', error);
            }
        };
        fetchEtfs(page);
    }, [page]);
    // 수익률 기준 정렬
    const sortedEtfs = [...etfData].sort((a, b) => b.returnRate - a.returnRate)

    // 상승률 상위 ETF
    const topGainers = [...etfData].sort((a, b) => b.change - a.change).slice(0, 3)

    // 하락률 상위 ETF
    const topLosers = [...etfData].sort((a, b) => a.change - b.change).slice(0, 3)

    const topThemes = useMemo(() => {
        if (etfData.length === 0) return [];

        // 테마별 데이터 분류 및 평균 수익률 계산
        const themeMap: Record<string, { totalReturn: number; count: number }> = {};

        etfData.forEach(etf => {
            if (!themeMap[etf.theme]) {
                themeMap[etf.theme] = {totalReturn: 0, count: 0};
            }
            themeMap[etf.theme].totalReturn += etf.returnRate;
            themeMap[etf.theme].count += 1;
        });

        const result = Object.entries(themeMap)
            .map(([theme, data]) => ({
                id: theme,
                name: theme, // 실제 이름이 필요하면 매핑 테이블 사용
                returnRate: data.totalReturn / data.count,
                etfCount: data.count,
            }))
            .sort((a, b) => b.returnRate - a.returnRate)
            .slice(0, 4); // 상위 4개만

        return result;
    }, [etfData]);

    const filteredEtfs = useMemo(() => {
        let filtered = [...etfData];

        // 테마 필터
        if (selectedTheme !== 'all') {
            filtered = filtered.filter(etf => etf.theme === selectedTheme);
        }

        // 검색 필터
        if (searchQuery.trim() !== '') {
            const query = searchQuery.toLowerCase();
            filtered = filtered.filter(etf =>
                etf.name.toLowerCase().includes(query) || etf.ticker.toLowerCase().includes(query)
            );
        }

        // 정렬
        filtered.sort((a, b) => {
            if (sortKey === 'price') return b.price - a.price;
            if (sortKey === 'change') return b.change - a.change;
            if (sortKey === 'volume') return b.volume - a.volume;
            return b.returnRate - a.returnRate;
        });

        return filtered;
    }, [etfData, searchQuery, selectedTheme, sortKey]);


    const handleLoadMore = () => {
        if (hasMore) {
            setPage(prev => prev + 1);
        }
    };


    return (
        <div className="container mx-auto py-6 px-4">
            {/* 히어로 섹션 */}
            <div className="mb-8 bg-gradient-to-r from-slate-900 to-slate-800 rounded-xl p-8 text-white">
                <div className="grid md:grid-cols-2 gap-8 items-center">
                    <div>
                        <h1 className="text-4xl font-bold mb-4">폭삭 벌었수다</h1>
                        <p className="text-xl mb-6">최고의 ETF 추천 서비스로 투자 수익을 극대화하세요</p>
                        <div className="flex gap-4">
                            <Button size="lg" className="bg-green-600 hover:bg-green-700">
                                <Link href="/recommendations">맞춤 ETF 추천받기</Link>
                            </Button>
                            <Button size="lg" variant="outline"
                                    className="bg-white text-slate-900 border-white hover:bg-slate-100">
                                <Link href="/register">무료 회원가입</Link>
                            </Button>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <Card className="bg-white/10 border-0">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg flex items-center gap-2">
                                    <TrendingUp className="h-5 w-5"/>
                                    최고 수익률
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div
                                    className="text-3xl font-bold text-green-400">{etfData.length > 0 ? `+${Math.max(...etfData.map(etf => etf.returnRate)).toFixed(1)}%` : '...'}</div>
                                <p className="text-sm text-white/70">  {
                                    etfData.length > 0
                                        ? etfData.reduce((prev, curr) =>
                                            curr.returnRate === Math.max(...etfData.map(e => e.returnRate)) ? curr : prev
                                        ).name
                                        : ''
                                }</p>
                            </CardContent>
                        </Card>
                        <Card className="bg-white/10 border-0">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg flex items-center gap-2">
                                    <BarChart3 className="h-5 w-5"/>
                                    평균 수익률
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="text-3xl font-bold text-green-400"> {etfData.length > 0
                                    ? `+${(etfData.reduce((sum, etf) => sum + etf.returnRate, 0) / etfData.length).toFixed(1)}%`
                                    : '...'}</div>
                                <p className="text-sm text-white/70">전체 ETF 기준</p>
                            </CardContent>
                        </Card>
                        <Card className="bg-white/10 border-0 col-span-2">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg">시장 요약</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div>
                                    <MarketTickerWidget/>
                                    {/*<div>*/}
                                    {/*  <p className="text-sm text-white/70">KOSPI</p>*/}
                                    {/*  <div className="flex items-center gap-1">*/}
                                    {/*    <span className="font-bold">{marketSummary.kospi.value.toLocaleString()}</span>*/}
                                    {/*    <span className={marketSummary.kospi.change >= 0 ? "text-green-400" : "text-red-400"}>*/}
                                    {/*      {marketSummary.kospi.change >= 0 ? "+" : ""}*/}
                                    {/*      {marketSummary.kospi.change}%*/}
                                    {/*    </span>*/}
                                    {/*  </div>*/}
                                    {/*</div>*/}
                                    {/*<div>*/}
                                    {/*  <p className="text-sm text-white/70">KOSDAQ</p>*/}
                                    {/*  <div className="flex items-center gap-1">*/}
                                    {/*    <span className="font-bold">{marketSummary.kosdaq.value.toLocaleString()}</span>*/}
                                    {/*    <span className={marketSummary.kosdaq.change >= 0 ? "text-green-400" : "text-red-400"}>*/}
                                    {/*      {marketSummary.kosdaq.change >= 0 ? "+" : ""}*/}
                                    {/*      {marketSummary.kosdaq.change}%*/}
                                    {/*    </span>*/}
                                    {/*  </div>*/}
                                    {/*</div>*/}
                                    {/*<div>*/}
                                    {/*  <p className="text-sm text-white/70">NASDAQ</p>*/}
                                    {/*  <div className="flex items-center gap-1">*/}
                                    {/*    <span className="font-bold">{marketSummary.nasdaq.value.toLocaleString()}</span>*/}
                                    {/*    <span className={marketSummary.nasdaq.change >= 0 ? "text-green-400" : "text-red-400"}>*/}
                                    {/*      {marketSummary.nasdaq.change >= 0 ? "+" : ""}*/}
                                    {/*      {marketSummary.nasdaq.change}%*/}
                                    {/*    </span>*/}
                                    {/*  </div>*/}
                                    {/*</div>*/}
                                    {/*<div>*/}
                                    {/*  <p className="text-sm text-white/70">S&P 500</p>*/}
                                    {/*  <div className="flex items-center gap-1">*/}
                                    {/*    <span className="font-bold">{marketSummary.sp500.value.toLocaleString()}</span>*/}
                                    {/*    <span className={marketSummary.sp500.change >= 0 ? "text-green-400" : "text-red-400"}>*/}
                                    {/*      {marketSummary.sp500.change >= 0 ? "+" : ""}*/}
                                    {/*      {marketSummary.sp500.change}%*/}
                                    {/*    </span>*/}
                                    {/*  </div>*/}
                                    {/*</div>*/}
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </div>

            {/* 검색 및 필터 */}
            <div className="mb-8 flex flex-col md:flex-row gap-4">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400"/>
                    <Input
                        placeholder="ETF 이름 또는 종목코드 검색"
                        className="pl-10"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>
                <div className="flex gap-2">
                    <Select value={selectedTheme} onValueChange={(val) => setSelectedTheme(val)}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="테마 선택"/>
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">전체</SelectItem>
                            {Array.from(new Set(etfData.map(etf => etf.theme))).map(theme => (
                                <SelectItem key={theme} value={theme}>
                                    {themeNameMap[theme] ?? theme}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <Select value={sortKey} onValueChange={(val) => setSortKey(val)}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="정렬 기준"/>
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="returnRate">수익률 순</SelectItem>
                            <SelectItem value="price">가격 순</SelectItem>
                            <SelectItem value="change">등락률 순</SelectItem>
                            <SelectItem value="volume">거래량 순</SelectItem>
                        </SelectContent>
                    </Select>

                    <Button variant="outline" size="icon">
                        <Filter className="h-4 w-4"/>
                    </Button>
                </div>
            </div>


            {/* 인기 테마 */}
            <div className="mb-8">
                <h2 className="text-2xl font-bold mb-4">인기 테마</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    {topThemes.map((theme) => (
                        <Link href={`/themes/${theme.id}`} key={theme.id}>
                            <Card className="p-4 bg-white shadow-sm rounded-lg hover:bg-slate-50 transition">
                                <CardHeader className="pb-2">
                                    <CardTitle className="text-lg font-semibold">
                                        {themeNameMap[theme.id] ?? theme.id}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-gray-500">평균 수익률</p>
                                    <div className="text-xl font-bold text-green-600">
                                        +{theme.returnRate.toFixed(1)}%
                                    </div>
                                    <p className="text-sm text-gray-400 mt-1">{theme.etfCount}개 ETF</p>
                                </CardContent>
                            </Card>
                        </Link>

                    ))}
                </div>
            </div>
            {/* 상승/하락 ETF */}
            <div className="mb-8 grid md:grid-cols-2 gap-6">
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <ArrowUpRight className="h-5 w-5 text-green-600"/>
                            상승률 상위 ETF
                        </CardTitle>
                        <CardDescription>오늘 가장 많이 상승한 ETF</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {topGainers.map((etf) => (
                                <Link href={`/etf/${etf.id}`} key={etf.id}>
                                    <div
                                        className="flex justify-between items-center p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                                        <div>
                                            <div className="font-medium">{etf.name}</div>
                                            <div className="text-sm text-slate-500">
                                                {etf.ticker} | {etf.theme}
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-green-600 font-bold">+{etf.change}%</div>
                                            <div className="text-sm">{etf.price.toLocaleString()}원</div>
                                        </div>
                                    </div>
                                </Link>

                            ))}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <ArrowDownRight className="h-5 w-5 text-red-600"/>
                            하락률 상위 ETF
                        </CardTitle>
                        <CardDescription>오늘 가장 많이 하락한 ETF</CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {topLosers.map((etf) => (
                                <Link href={`/etf/${etf.id}`} key={etf.id}>
                                    <div
                                        className="flex justify-between items-center p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
                                        <div>
                                            <div className="font-medium">{etf.name}</div>
                                            <div className="text-sm text-slate-500">
                                                {etf.ticker} | {etf.theme}
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-red-600 font-bold">{etf.change}%</div>
                                            <div className="text-sm">{etf.price.toLocaleString()}원</div>
                                        </div>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* ETF 랭킹 테이블 */}
            <div className="mb-8">
                <Tabs defaultValue="all">
                    <div className="flex justify-between items-center mb-4">
                        <h2 className="text-2xl font-bold">ETF 수익 랭킹</h2>
                    </div>

                    <TabsContent value="all">
                        <Card>
                            <CardContent className="p-0">
                                <Table>
                                    <TableHeader>
                                        <TableRow>
                                            <TableHead>순위</TableHead>
                                            <TableHead>ETF명</TableHead>
                                            <TableHead>종목코드</TableHead>
                                            <TableHead>테마</TableHead>
                                            <TableHead className="text-right">현재가</TableHead>
                                            <TableHead className="text-right">등락률</TableHead>
                                            <TableHead className="text-right">거래량</TableHead>
                                            <TableHead className="text-right">수익률</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        <EtfCard etfs={filteredEtfs}/>
                                    </TableBody>
                                </Table>
                            </CardContent>

                            <CardFooter className="flex justify-center py-4">
                                {hasMore && (
                                    <Button variant="outline" onClick={handleLoadMore}>
                                        더 보기
                                    </Button>
                                )}
                            </CardFooter>
                        </Card>
                    </TabsContent>

                </Tabs>
            </div>

            {/* 추천 섹션 */}
            <div className="mb-8">
                <div className="bg-slate-50 rounded-xl p-6">
                    <div className="text-center mb-6">
                        <h2 className="text-2xl font-bold mb-2">나만의 맞춤 ETF 추천</h2>
                        <p className="text-slate-500 max-w-2xl mx-auto">
                            투자 성향과 목표에 맞는 ETF를 추천받아 더 효율적인 투자를 시작하세요. 회원가입 후 무료로 이용 가능합니다.
                        </p>
                    </div>
                    <div className="flex justify-center gap-4">
                        <Button size="lg" className="bg-green-600 hover:bg-green-700">
                            <Link href="/recommendations">맞춤 ETF 추천받기</Link>
                        </Button>
                        <Button size="lg" variant="outline"
                                className="bg-white text-slate-900 border-slate-300 hover:bg-slate-100">
                            <Link href="/register">무료 회원가입</Link>
                            {/*{filteredEtfs.map((etf) => (*/}
                            {/*    <EtfCard key={etf.id} etf={etf} />*/}
                            {/*))}*/}
                        </Button>
                    </div>
                </div>
            </div>

            <section className="mt-12">
                <h1>ETF 실시간 시세</h1>
                {/* 페이지네이션 버튼 */}
                <Pagination size={size} onPageChange={setWebsocketPage} />
                {/* 실시간 시세 테이블 */}
                <StocksTable page={websocketPage} size={size} />
            </section>
        </div>
    )
}
