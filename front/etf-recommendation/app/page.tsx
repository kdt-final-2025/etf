"use client"
import {useEffect, useMemo, useState, useCallback} from "react"
import Link from "next/link"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card"
import {Table, TableBody, TableHead, TableHeader, TableRow} from "@/components/ui/table"
import {Button} from "@/components/ui/button"
import {Tabs, TabsContent} from "@/components/ui/tabs"
import {TrendingUp, BarChart3, ArrowUpRight, ArrowDownRight, Filter} from "lucide-react"
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select"
import EtfCard, {type ETF} from "@/components/EtfCard"
import MarketTickerWidget from "@/components/MarketTickerWidget"
import {fetchEtfs} from "@/lib/api/etf"
import EnhancedSearchDropdown from "@/components/enhanced-search-dropdown"

// 시장 요약 데이터
const marketSummary = {
    kospi: {value: 2850.12, change: 1.2},
    kosdaq: {value: 920.45, change: -0.5},
    nasdaq: {value: 16250.8, change: 0.8},
    sp500: {value: 5120.35, change: 0.6},
}

const themeNameMap: Record<string, string> = {
    AI_DATA: "AI 데이터",
    USA: "미국",
    KOREA: "한국",
    REITS: "리츠",
    MULTI_ASSET: "멀티에셋",
    COMMODITIES: "원자재",
    HIGH_RISK: "고위험",
    SECTOR: "섹터",
    DIVIDEND: "배당",
    ESG: "ESG",
    GOLD: "금",
    GOVERNMENT_BOND: "국채",
    CORPORATE_BOND: "회사채",
    DEFENSE: "방위산업",
    SEMICONDUCTOR: "반도체",
    BIO: "바이오",
    EMERGING_MARKETS: "신흥시장",
}

export default function Home() {
    const [searchQuery, setSearchQuery] = useState("")
    const [selectedTheme, setSelectedTheme] = useState("all")
    const [sortKey, setSortKey] = useState("returnRate")

    // 단일 데이터 소스로 통합
    const [allEtfData, setAllEtfData] = useState<ETF[]>([])
    const [displayedEtfs, setDisplayedEtfs] = useState<ETF[]>([])
    const [currentPage, setCurrentPage] = useState(1)
    const [loading, setLoading] = useState(false)
    const [hasMore, setHasMore] = useState(true)

    const ITEMS_PER_PAGE = 20

    // 전체 데이터 로딩 (한 번만)
    useEffect(() => {
        const fetchAllEtfsData = async () => {
            setLoading(true)
            try {
                const {data, error} = await fetchEtfs({
                    size: 10000,
                    period: "weekly",
                })

                if (error || !data) {
                    console.error("전체 ETF 로딩 실패", error)
                    return
                }

                const allEtfs: ETF[] = data.etfReadResponseList.map((etf: any, index: number) => ({
                    id: etf.etfId,
                    name: etf.etfName,
                    ticker: etf.etfCode,
                    theme: etf.theme,
                    price: 10000 + index * 100,
                    change: Number.parseFloat((Math.random() * 5).toFixed(2)) * (Math.random() > 0.5 ? 1 : -1),
                    volume: Math.floor(Math.random() * 100000),
                    returnRate: etf.returnRate,
                }))

                setAllEtfData(allEtfs)
            } catch (error) {
                console.error("전체 ETF 로딩 실패", error)
            } finally {
                setLoading(false)
            }
        }

        fetchAllEtfsData()
    }, [])

    // 필터링된 데이터 계산
    const filteredAndSortedEtfs = useMemo(() => {
        let result = [...allEtfData]

        // 테마 필터링
        if (selectedTheme !== "all") {
            result = result.filter((e) => e.theme === selectedTheme)
        }

        // 검색 필터링
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase()
            result = result.filter((e) =>
                e.name.toLowerCase().includes(query) ||
                e.ticker.toLowerCase().includes(query)
            )
        }

        // 정렬
        result.sort((a, b) => {
            const valueA = a[sortKey as keyof ETF]
            const valueB = b[sortKey as keyof ETF]

            if (typeof valueA === "number" && typeof valueB === "number") {
                return valueB - valueA
            }

            return String(valueB).localeCompare(String(valueA))
        })

        return result
    }, [allEtfData, selectedTheme, searchQuery, sortKey])

    // 현재 표시할 데이터 업데이트
    useEffect(() => {
        const startIndex = 0
        const endIndex = currentPage * ITEMS_PER_PAGE
        const newDisplayedEtfs = filteredAndSortedEtfs.slice(startIndex, endIndex)

        setDisplayedEtfs(newDisplayedEtfs)
        setHasMore(endIndex < filteredAndSortedEtfs.length)
    }, [filteredAndSortedEtfs, currentPage])

    // 필터나 검색이 변경될 때 페이지 초기화
    useEffect(() => {
        setCurrentPage(1)
    }, [selectedTheme, searchQuery, sortKey])

    // 더보기 핸들러
    const handleLoadMore = useCallback(() => {
        if (hasMore && !loading) {
            setCurrentPage(prev => prev + 1)
        }
    }, [hasMore, loading])

    // 상위/하위 ETF 계산
    const sortedByChange = useMemo(() => {
        return allEtfData
            .filter((etf) => typeof etf.change === "number" && !isNaN(etf.change))
            .slice()
    }, [allEtfData])

    const topGainers = useMemo(() => {
        return sortedByChange
            .sort((a, b) => b.change - a.change)
            .slice(0, 5)
    }, [sortedByChange])

    const topLosers = useMemo(() => {
        return sortedByChange
            .sort((a, b) => a.change - b.change)
            .slice(0, 5)
    }, [sortedByChange])

    // 테마별 데이터 분류 및 평균 수익률 계산
    const topThemes = useMemo(() => {
        const map: Record<string, { total: number; count: number }> = {}

        allEtfData.forEach((etf) => {
            if (!map[etf.theme]) map[etf.theme] = {total: 0, count: 0}
            map[etf.theme].total += etf.returnRate
            map[etf.theme].count += 1
        })

        return Object.entries(map)
            .map(([theme, {total, count}]) => ({
                id: theme,
                name: theme,
                returnRate: total / count,
                etfCount: count,
            }))
            .sort((a, b) => b.returnRate - a.returnRate)
            .slice(0, 4)
    }, [allEtfData])

    // ETF 선택 핸들러
    const handleEtfSelect = useCallback((item: ETF) => {
        setSearchQuery(item.name)
    }, [])

    return (
        <div className="container mx-auto py-6 px-4">

            {/* 히어로 섹션 */}
            <div
                className="mb-8 bg-gradient-to-r from-gray-100 to-gray-200 rounded-xl p-8 text-gray-900 dark:from-slate-900 dark:to-slate-800 dark:text-white">
                <div className="grid md:grid-cols-2 gap-8 items-center">
                    <div>
                        <h1 className="text-4xl font-bold mb-4">FIETA</h1>
                        <p className="text-xl mb-6">최고의 AI ETF 추천 서비스로 투자 수익을 극대화하세요</p>
                        <div className="flex gap-4">
                            <Button size="lg" className="bg-green-700 hover:bg-green-800 text-white">
                                <Link href="/recommendations">맞춤 ETF 추천받기</Link>
                            </Button>
                            <Button
                                size="lg"
                                variant="outline"
                                className="bg-white text-gray-900 border-gray-900 hover:bg-gray-100 dark:bg-gray-800 dark:text-gray-100 dark:border-gray-600 dark:hover:bg-gray-700"
                            >
                                <Link href="/register">무료 회원가입</Link>
                            </Button>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">

                        {/* 최고 수익률 카드 */}
                        <Card
                            className="bg-white shadow-md border border-gray-200 dark:bg-gray-800 dark:border-gray-700">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg flex items-center gap-2 text-gray-900 dark:text-gray-100">
                                    <TrendingUp className="h-5 w-5"/>
                                    주간 최고 수익률 ETF
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                {allEtfData.length > 0 ? (() => {
                                    // allEtfData를 복사하여 원본을 변경하지 않고, returnRate 기준으로 정렬
                                    const sortedEtfs = [...allEtfData].sort((a, b) => {
                                        // 안전하게 유효한 숫자만 비교하도록 합니다.
                                        const valA = typeof a.returnRate === 'number' && !isNaN(a.returnRate) ? a.returnRate : -Infinity;
                                        const valB = typeof b.returnRate === 'number' && !isNaN(b.returnRate) ? b.returnRate : -Infinity;
                                        return valB - valA; // 내림차순 정렬
                                    });

                                    const topEtf = sortedEtfs[0]; // 가장 높은 수익률을 가진 ETF

                                    // 표시될 수익률 값과 이름 결정
                                    const displayReturnRate = topEtf && typeof topEtf.returnRate === 'number' && !isNaN(topEtf.returnRate)
                                        ? topEtf.returnRate.toFixed(2) // 소수점 둘째 자리까지 표시
                                        : "0.00"; // 기본값 설정

                                    return (
                                        <>
                                            <div className="text-3xl font-bold text-green-700 dark:text-green-400">
                                                +{displayReturnRate}%
                                            </div>
                                            <p className="text-sm text-gray-700 dark:text-gray-300">
                                                {topEtf?.name || "데이터 없음"}
                                            </p>
                                        </>
                                    );
                                })() : (
                                    // 데이터 로딩 중이거나 없을 때
                                    <>
                                        <div className="text-3xl font-bold text-green-700 dark:text-green-400">...</div>
                                        <p className="text-sm text-gray-700 dark:text-gray-300">데이터 로딩 중</p>
                                    </>
                                )}
                            </CardContent>
                        </Card>

                        {/* 평균 수익률 카드 */}
                        <Card
                            className="bg-white shadow-md border border-gray-200 dark:bg-gray-800 dark:border-gray-700">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg flex items-center gap-2 text-gray-900 dark:text-gray-100">
                                    <BarChart3 className="h-5 w-5"/>
                                    주간 평균 수익률
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div className="text-3xl font-bold text-green-700 dark:text-green-400">
                                    {allEtfData.length > 0
                                        ? (() => {
                                            // 유효한 returnRate만 필터링하여 합산
                                            const validEtfs = allEtfData.filter(etf => typeof etf.returnRate === 'number' && !isNaN(etf.returnRate));
                                            const sumReturnRate = validEtfs.reduce((sum, etf) => sum + etf.returnRate, 0);
                                            const averageReturnRate = validEtfs.length > 0 ? sumReturnRate / validEtfs.length : 0;

                                            // 소수점 둘째 자리까지 표시
                                            const displayAvgReturn = averageReturnRate.toFixed(2);

                                            // 계산된 평균이 0.00%일 경우 부호 없이 표시
                                            if (parseFloat(displayAvgReturn) === 0) {
                                                return `0.00%`;
                                            }

                                            return `${averageReturnRate >= 0 ? '+' : ''}${displayAvgReturn}%`;
                                        })()
                                        : "..."}
                                </div>
                                <p className="text-sm text-gray-700 dark:text-gray-300">전체 ETF 기준</p>
                            </CardContent>
                        </Card>


                        {/* 시장 요약 카드 */}
                        <Card
                            className="bg-white shadow-md border border-gray-200 col-span-2 dark:bg-gray-800 dark:border-gray-700">
                            <CardHeader className="pb-2">
                                <CardTitle className="text-lg text-gray-900 dark:text-gray-100">시장 요약</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <div>
                                    <MarketTickerWidget/>
                                </div>
                            </CardContent>
                        </Card>
                    </div>
                </div>
            </div>


            {/* 검색 및 필터 */}
            <div className="mb-8 flex flex-col md:flex-row gap-4">
                <div className="flex-1">
                    <EnhancedSearchDropdown
                        items={allEtfData}
                        onSelect={handleEtfSelect}
                        placeholder="ETF 이름 또는 종목코드 검색"
                        showRecent={true}
                        className="bg-white text-gray-900 dark:bg-gray-800 dark:text-gray-100 border border-gray-300 dark:border-gray-700 rounded-md"
                    />
                </div>
                <div className="flex gap-2">
                    <Select
                        value={selectedTheme}
                        onValueChange={setSelectedTheme}
                        className="bg-white text-gray-900 dark:bg-gray-800 dark:text-gray-100 border border-gray-300 dark:border-gray-700 rounded-md"
                    >
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="테마 선택"/>
                        </SelectTrigger>
                        <SelectContent className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                            <SelectItem value="all">전체</SelectItem>
                            {Array.from(new Set(allEtfData.map((etf) => etf.theme))).map((theme) => (
                                <SelectItem key={theme} value={theme}>
                                    {themeNameMap[theme] ?? theme}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    <Select
                        value={sortKey}
                        onValueChange={setSortKey}
                        className="bg-white text-gray-900 dark:bg-gray-800 dark:text-gray-100 border border-gray-300 dark:border-gray-700 rounded-md"
                    >
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="정렬 기준"/>
                        </SelectTrigger>
                        <SelectContent className="bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100">
                            <SelectItem value="returnRate">수익률 순</SelectItem>
                            <SelectItem value="price">가격 순</SelectItem>
                            <SelectItem value="change">등락률 순</SelectItem>
                            <SelectItem value="volume">거래량 순</SelectItem>
                        </SelectContent>
                    </Select>

                    <Button
                        variant="outline"
                        size="icon"
                        className="border-gray-300 text-gray-900 hover:bg-gray-100 dark:border-gray-700 dark:text-gray-100 dark:hover:bg-gray-700"
                    >
                        <Filter className="h-4 w-4"/>
                    </Button>
                </div>
            </div>


            {/* 인기 테마 */}
            <div className="mb-8">
                <h2 className="text-2xl font-bold mb-4 text-gray-900 dark:text-gray-100">인기 테마</h2>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    {topThemes.map((theme) => (
                        <Link href={`/themes/${theme.id}`} key={theme.id}>
                            <Card
                                className="p-4 bg-white dark:bg-gray-800 shadow-sm rounded-lg hover:bg-slate-50 dark:hover:bg-gray-700 transition border border-gray-200 dark:border-gray-700">
                                <CardHeader className="pb-2">
                                    <CardTitle className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                                        {themeNameMap[theme.id] ?? theme.id}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-sm text-gray-500 dark:text-gray-400">주간 평균 수익률</p>
                                    <div className="text-xl font-bold text-green-600 dark:text-green-400">
                                        +{theme.returnRate.toFixed(1)}%
                                    </div>
                                    <p className="text-sm text-gray-400 dark:text-gray-500 mt-1">
                                        {theme.etfCount}개 ETF
                                    </p>
                                </CardContent>
                            </Card>
                        </Link>
                    ))}
                </div>
            </div>

            {/* 상승/하락 ETF */}
            <div className="mb-8 grid md:grid-cols-2 gap-6">
                <Card className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-gray-900 dark:text-white">
                            <ArrowUpRight className="h-5 w-5 text-green-600"/>
                            실시간 상승률 상위 ETF
                        </CardTitle>
                        <CardDescription className="text-gray-500 dark:text-gray-400">
                            오늘 가장 많이 상승한 ETF
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {topGainers.map((etf) => (
                                <Link href={`/etf/${etf.id}`} key={etf.id}>
                                    <div
                                        className="flex justify-between items-center p-3 border border-gray-200 dark:border-gray-600 rounded-lg hover:bg-slate-50 dark:hover:bg-gray-700 cursor-pointer">
                                        <div>
                                            <div className="font-medium text-gray-900 dark:text-white">{etf.name}</div>
                                            <div className="text-sm text-slate-500 dark:text-gray-400">
                                                {etf.ticker} | {etf.theme}
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-green-600 font-bold">+{etf.change}%</div>
                                            <div
                                                className="text-sm text-gray-600 dark:text-gray-300">{etf.price.toLocaleString()}원
                                            </div>
                                        </div>
                                    </div>
                                </Link>
                            ))}
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2 text-gray-900 dark:text-white">
                            <ArrowDownRight className="h-5 w-5 text-red-600"/>
                            실시간 하락률 상위 ETF
                        </CardTitle>
                        <CardDescription className="text-gray-500 dark:text-gray-400">
                            오늘 가장 많이 하락한 ETF
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {topLosers.map((etf) => (
                                <Link href={`/etf/${etf.id}`} key={etf.id}>
                                    <div
                                        className="flex justify-between items-center p-3 border border-gray-200 dark:border-gray-600 rounded-lg hover:bg-slate-50 dark:hover:bg-gray-700 cursor-pointer">
                                        <div>
                                            <div className="font-medium text-gray-900 dark:text-white">{etf.name}</div>
                                            <div className="text-sm text-slate-500 dark:text-gray-400">
                                                {etf.ticker} | {etf.theme}
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-red-600 font-bold">{etf.change}%</div>
                                            <div
                                                className="text-sm text-gray-600 dark:text-gray-300">{etf.price.toLocaleString()}원
                                            </div>
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
                        <h2 className="text-2xl font-bold text-gray-900 dark:text-white">ETF 수익 랭킹</h2>
                        <div className="text-sm text-gray-500 dark:text-gray-400">
                            전체 {filteredAndSortedEtfs.length}개 중 {displayedEtfs.length}개 표시
                        </div>
                    </div>

                    <TabsContent value="all">
                        <Card className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700">
                            <CardContent className="p-0">
                                <Table className="text-gray-900 dark:text-white">
                                    <TableHeader className="bg-gray-100 dark:bg-gray-700">
                                        <TableRow>
                                            <TableHead>순위</TableHead>
                                            <TableHead>ETF명</TableHead>
                                            <TableHead>종목코드</TableHead>
                                            <TableHead>테마</TableHead>
                                            <TableHead className="text-right">현재가</TableHead>
                                            <TableHead className="text-right">등락률</TableHead>
                                            <TableHead className="text-right">거래량</TableHead>
                                            <TableHead className="text-right">수익률(주간)</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        <EtfCard etfs={displayedEtfs}/>
                                    </TableBody>
                                </Table>
                            </CardContent>

                            <CardFooter className="flex justify-center py-4">
                                {hasMore && (
                                    <Button
                                        variant="outline"
                                        onClick={handleLoadMore}
                                        disabled={loading}
                                        className="text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-700"
                                    >
                                        {loading ? "로딩 중..." : "더 보기"}
                                    </Button>
                                )}
                                {!hasMore && displayedEtfs.length > 0 && (
                                    <p className="text-sm text-gray-500 dark:text-gray-400">모든 데이터를 표시했습니다.</p>
                                )}
                            </CardFooter>
                        </Card>
                    </TabsContent>
                </Tabs>
            </div>

        </div>
    )
}