"use client"
import { useEffect, useState } from 'react'
import Link from "next/link"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Search, TrendingUp, BarChart3, ArrowUpRight, ArrowDownRight, Filter } from "lucide-react"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

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
  kospi: { value: 2850.12, change: 1.2 },
  kosdaq: { value: 920.45, change: -0.5 },
  nasdaq: { value: 16250.8, change: 0.8 },
  sp500: { value: 5120.35, change: 0.6 },
}

// 인기 테마 데이터
const popularThemes = [
  { id: "tech", name: "기술", returnRate: 24.5, etfCount: 12 },
  { id: "energy", name: "에너지", returnRate: 22.8, etfCount: 8 },
  { id: "healthcare", name: "헬스케어", returnRate: 18.2, etfCount: 10 },
  { id: "global", name: "글로벌", returnRate: 16.5, etfCount: 15 },
]


export default function Home() {
  const [etfData, setEtfData] = useState<ETF[]>([])
  console.log(etfData)
  useEffect(() => {
    const fetchEtfs = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/v1/etfs?page=1&size=20&period=weekly')
        if (!response.ok) throw new Error('데이터 로드 실패')
        const data = await response.json()

        const transformedData: ETF[] = data.etfReadResponseList.map((etf: any, index: number) => ({
          id: etf.etfId,
          name: etf.etfName,
          ticker: etf.etfCode,
          theme: etf.theme,
          price: 10000 + index * 100,
          change: parseFloat((Math.random() * 5).toFixed(2)) * (Math.random() > 0.5 ? 1 : -1),
          volume: Math.floor(Math.random() * 100000),
          returnRate: etf.returnRate,
        }))

        setEtfData(transformedData)
      } catch (error) {
        console.error('ETF 데이터 로딩 에러:', error)
      }
    }

    fetchEtfs()
  }, [])
  // 수익률 기준 정렬
  const sortedEtfs = [...etfData].sort((a, b) => b.returnRate - a.returnRate)

  // 상승률 상위 ETF
  const topGainers = [...etfData].sort((a, b) => b.change - a.change).slice(0, 3)

  // 하락률 상위 ETF
  const topLosers = [...etfData].sort((a, b) => a.change - b.change).slice(0, 3)



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
              <Button size="lg" variant="outline" className="bg-white text-slate-900 border-white hover:bg-slate-100">
                <Link href="/register">무료 회원가입</Link>
              </Button>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Card className="bg-white/10 border-0">
              <CardHeader className="pb-2">
                <CardTitle className="text-lg flex items-center gap-2">
                  <TrendingUp className="h-5 w-5" />
                  최고 수익률
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-green-400">+28.5%</div>
                <p className="text-sm text-white/70">KODEX 삼성전자</p>
              </CardContent>
            </Card>
            <Card className="bg-white/10 border-0">
              <CardHeader className="pb-2">
                <CardTitle className="text-lg flex items-center gap-2">
                  <BarChart3 className="h-5 w-5" />
                  평균 수익률
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-green-400">+18.6%</div>
                <p className="text-sm text-white/70">전체 ETF 기준</p>
              </CardContent>
            </Card>
            <Card className="bg-white/10 border-0 col-span-2">
              <CardHeader className="pb-2">
                <CardTitle className="text-lg">시장 요약</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-4 gap-2">
                  <div>
                    <p className="text-sm text-white/70">KOSPI</p>
                    <div className="flex items-center gap-1">
                      <span className="font-bold">{marketSummary.kospi.value.toLocaleString()}</span>
                      <span className={marketSummary.kospi.change >= 0 ? "text-green-400" : "text-red-400"}>
                        {marketSummary.kospi.change >= 0 ? "+" : ""}
                        {marketSummary.kospi.change}%
                      </span>
                    </div>
                  </div>
                  <div>
                    <p className="text-sm text-white/70">KOSDAQ</p>
                    <div className="flex items-center gap-1">
                      <span className="font-bold">{marketSummary.kosdaq.value.toLocaleString()}</span>
                      <span className={marketSummary.kosdaq.change >= 0 ? "text-green-400" : "text-red-400"}>
                        {marketSummary.kosdaq.change >= 0 ? "+" : ""}
                        {marketSummary.kosdaq.change}%
                      </span>
                    </div>
                  </div>
                  <div>
                    <p className="text-sm text-white/70">NASDAQ</p>
                    <div className="flex items-center gap-1">
                      <span className="font-bold">{marketSummary.nasdaq.value.toLocaleString()}</span>
                      <span className={marketSummary.nasdaq.change >= 0 ? "text-green-400" : "text-red-400"}>
                        {marketSummary.nasdaq.change >= 0 ? "+" : ""}
                        {marketSummary.nasdaq.change}%
                      </span>
                    </div>
                  </div>
                  <div>
                    <p className="text-sm text-white/70">S&P 500</p>
                    <div className="flex items-center gap-1">
                      <span className="font-bold">{marketSummary.sp500.value.toLocaleString()}</span>
                      <span className={marketSummary.sp500.change >= 0 ? "text-green-400" : "text-red-400"}>
                        {marketSummary.sp500.change >= 0 ? "+" : ""}
                        {marketSummary.sp500.change}%
                      </span>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* 검색 및 필터 */}
      <div className="mb-8 flex flex-col md:flex-row gap-4">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400" />
          <Input placeholder="ETF 이름 또는 종목코드 검색" className="pl-10" />
        </div>
        <div className="flex gap-2">
          <Select>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="테마 선택" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">전체</SelectItem>
              <SelectItem value="tech">기술</SelectItem>
              <SelectItem value="finance">금융</SelectItem>
              <SelectItem value="healthcare">헬스케어</SelectItem>
              <SelectItem value="consumer">소비재</SelectItem>
              <SelectItem value="energy">에너지</SelectItem>
              <SelectItem value="global">글로벌</SelectItem>
            </SelectContent>
          </Select>
          <Select>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="정렬 기준" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="returnRate">수익률 순</SelectItem>
              <SelectItem value="price">가격 순</SelectItem>
              <SelectItem value="change">등락률 순</SelectItem>
              <SelectItem value="volume">거래량 순</SelectItem>
            </SelectContent>
          </Select>
          <Button variant="outline" size="icon">
            <Filter className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* 인기 테마 */}
      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">인기 테마</h2>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {popularThemes.map((theme) => (
            <Link href={`/themes/${theme.id}`} key={theme.id}>
              <Card className="hover:shadow-lg transition-shadow cursor-pointer h-full">
                <CardHeader className="pb-2">
                  <CardTitle>{theme.name}</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold text-green-600">+{theme.returnRate}%</div>
                  <p className="text-sm text-slate-500">평균 수익률</p>
                </CardContent>
                <CardFooter className="text-sm text-slate-500">ETF {theme.etfCount}개</CardFooter>
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
              <ArrowUpRight className="h-5 w-5 text-green-600" />
              상승률 상위 ETF
            </CardTitle>
            <CardDescription>오늘 가장 많이 상승한 ETF</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topGainers.map((etf) => (
                    <Link href={`/etf/${etf.id}`} key={etf.id} >
                      <div  className="flex justify-between items-center p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
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
              <ArrowDownRight className="h-5 w-5 text-red-600" />
              하락률 상위 ETF
            </CardTitle>
            <CardDescription>오늘 가장 많이 하락한 ETF</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topLosers.map((etf) => (
                <Link href={`/etf/${etf.id}`} key={etf.id}>
                  <div className="flex justify-between items-center p-3 border rounded-lg hover:bg-slate-50 cursor-pointer">
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
            <TabsList>
              <TabsTrigger value="all">전체</TabsTrigger>
              <TabsTrigger value="tech">기술</TabsTrigger>
              <TabsTrigger value="finance">금융</TabsTrigger>
              <TabsTrigger value="energy">에너지</TabsTrigger>
            </TabsList>
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
                    {sortedEtfs.map((etf, index) => (
                        <TableRow key={etf.id} className="cursor-pointer hover:bg-slate-50">
                          <TableCell className="font-medium">{index + 1}</TableCell>
                          <TableCell>
                            <Link href={`/etf/${etf.id}`} className="hover:underline text-blue-600">
                              {etf.name}
                            </Link>
                        </TableCell>
                        <TableCell>{etf.ticker}</TableCell>
                        <TableCell>
                          <Badge variant="outline">{etf.theme}</Badge>
                        </TableCell>
                        <TableCell className="text-right">{etf.price.toLocaleString()}원</TableCell>
                        <TableCell className={`text-right ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                          {etf.change >= 0 ? "+" : ""}
                          {etf.change}%
                        </TableCell>
                        <TableCell className="text-right">{etf.volume.toLocaleString()}</TableCell>
                        <TableCell className="text-right font-bold text-green-600">+{etf.returnRate}%</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
              <CardFooter className="flex justify-center py-4">
                <Button variant="outline">더 보기</Button>
              </CardFooter>
            </Card>
          </TabsContent>

          <TabsContent value="tech">
            <Card>
              <CardContent className="p-0">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>순위</TableHead>
                      <TableHead>ETF명</TableHead>
                      <TableHead>종목코드</TableHead>
                      <TableHead className="text-right">현재가</TableHead>
                      <TableHead className="text-right">등락률</TableHead>
                      <TableHead className="text-right">거래량</TableHead>
                      <TableHead className="text-right">수익률</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {sortedEtfs
                      .filter((etf) => etf.theme === "기술")
                      .map((etf, index) => (
                        <TableRow key={etf.id} className="cursor-pointer hover:bg-slate-50">
                          <TableCell className="font-medium">{index}</TableCell>
                          <TableCell>
                            <Link href={`/etf/${etf.id}`} className="hover:underline text-blue-600">
                              {etf.name}
                            </Link>
                          </TableCell>
                          <TableCell>{etf.ticker}</TableCell>
                          <TableCell className="text-right">{etf.price.toLocaleString()}원</TableCell>
                          <TableCell className={`text-right ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                            {etf.change >= 0 ? "+" : ""}
                            {etf.change}%
                          </TableCell>
                          <TableCell className="text-right">{etf.volume.toLocaleString()}</TableCell>
                          <TableCell className="text-right font-bold text-green-600">+{etf.returnRate}%</TableCell>
                        </TableRow>
                      ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="finance">
            <Card>
              <CardContent className="p-0">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>순위</TableHead>
                      <TableHead>ETF명</TableHead>
                      <TableHead>종목코드</TableHead>
                      <TableHead className="text-right">현재가</TableHead>
                      <TableHead className="text-right">등락률</TableHead>
                      <TableHead className="text-right">거래량</TableHead>
                      <TableHead className="text-right">수익률</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {sortedEtfs
                      .filter((etf) => etf.theme === "금융")
                      .map((etf, index) => (
                        <TableRow key={etf.id} className="cursor-pointer hover:bg-slate-50">
                          <TableCell className="font-medium">{index + 1}</TableCell>
                          <TableCell>
                            <Link href={`/etf/${etf.id}`} className="hover:underline text-blue-600">
                              {etf.name}
                            </Link>
                          </TableCell>
                          <TableCell>{etf.ticker}</TableCell>
                          <TableCell className="text-right">{etf.price.toLocaleString()}원</TableCell>
                          <TableCell className={`text-right ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                            {etf.change >= 0 ? "+" : ""}
                            {etf.change}%
                          </TableCell>
                          <TableCell className="text-right">{etf.volume.toLocaleString()}</TableCell>
                          <TableCell className="text-right font-bold text-green-600">+{etf.returnRate}%</TableCell>
                        </TableRow>
                      ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="energy">
            <Card>
              <CardContent className="p-0">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>순위</TableHead>
                      <TableHead>ETF명</TableHead>
                      <TableHead>종목코드</TableHead>
                      <TableHead className="text-right">현재가</TableHead>
                      <TableHead className="text-right">등락률</TableHead>
                      <TableHead className="text-right">거래량</TableHead>
                      <TableHead className="text-right">수익률</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {sortedEtfs
                      .filter((etf) => etf.theme === "에너지")
                      .map((etf, index) => (
                        <TableRow key={etf.id} className="cursor-pointer hover:bg-slate-50">
                          <TableCell className="font-medium">{index + 1}</TableCell>
                          <TableCell>
                            <Link href={`/etf/${etf.id}`} className="hover:underline text-blue-600">
                              {etf.name}
                            </Link>
                          </TableCell>
                          <TableCell>{etf.ticker}</TableCell>
                          <TableCell className="text-right">{etf.price.toLocaleString()}원</TableCell>
                          <TableCell className={`text-right ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                            {etf.change >= 0 ? "+" : ""}
                            {etf.change}%
                          </TableCell>
                          <TableCell className="text-right">{etf.volume.toLocaleString()}</TableCell>
                          <TableCell className="text-right font-bold text-green-600">+{etf.returnRate}%</TableCell>
                        </TableRow>
                      ))}
                  </TableBody>
                </Table>
              </CardContent>
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
            <Button size="lg" variant="outline" className="bg-white text-slate-900 border-slate-300 hover:bg-slate-100">
              <Link href="/register">무료 회원가입</Link>
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
