import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { notFound } from "next/navigation"
import Link from "next/link"

// 샘플 ETF 데이터
const etfData = {
  tech: [
    {
      id: 1,
      name: "KODEX 삼성전자",
      ticker: "005930",
      theme: "기술",
      returnRate: 28.5,
      price: 82500,
      change: 2.1,
    },
    {
      id: 4,
      name: "ARIRANG 글로벌4차산업",
      ticker: "289480",
      theme: "기술",
      returnRate: 21.2,
      price: 9850,
      change: 1.2,
    },
    {
      id: 9,
      name: "TIGER IT테크",
      ticker: "365040",
      theme: "기술",
      returnRate: 14.8,
      price: 12700,
      change: 0.7,
    },
  ],
  finance: [
    {
      id: 6,
      name: "KODEX 은행",
      ticker: "091170",
      theme: "금융",
      returnRate: 18.9,
      price: 11200,
      change: -0.3,
    },
    {
      id: 10,
      name: "KINDEX 금융",
      ticker: "272560",
      theme: "금융",
      returnRate: 12.5,
      price: 9800,
      change: -0.5,
    },
  ],
  healthcare: [
    {
      id: 3,
      name: "KODEX 바이오",
      ticker: "244580",
      theme: "헬스케어",
      returnRate: 22.3,
      price: 15600,
      change: 0.9,
    },
    {
      id: 11,
      name: "TIGER 헬스케어",
      ticker: "227540",
      theme: "헬스케어",
      returnRate: 11.2,
      price: 13400,
      change: 0.3,
    },
  ],
  consumer: [
    {
      id: 8,
      name: "KINDEX 필수소비재",
      ticker: "266370",
      theme: "소비재",
      returnRate: 15.2,
      price: 13400,
      change: 0.2,
    },
    {
      id: 12,
      name: "KODEX 소비자재",
      ticker: "266410",
      theme: "소비재",
      returnRate: 10.8,
      price: 10200,
      change: -0.1,
    },
  ],
  energy: [
    {
      id: 2,
      name: "TIGER 2차전지",
      ticker: "305720",
      theme: "에너지",
      returnRate: 25.7,
      price: 42300,
      change: 1.8,
    },
    {
      id: 7,
      name: "TIGER 차이나전기차",
      ticker: "371460",
      theme: "에너지",
      returnRate: 17.5,
      price: 8750,
      change: -0.8,
    },
  ],
  global: [
    {
      id: 5,
      name: "TIGER 미국나스닥100",
      ticker: "133690",
      theme: "글로벌",
      returnRate: 20.8,
      price: 21500,
      change: 0.5,
    },
    {
      id: 13,
      name: "KODEX 선진국MSCI",
      ticker: "251350",
      theme: "글로벌",
      returnRate: 9.5,
      price: 11800,
      change: 0.2,
    },
  ],
}

// 테마 이름 매핑
const themeNames = {
  tech: "기술",
  finance: "금융",
  healthcare: "헬스케어",
  consumer: "소비재",
  energy: "에너지",
  global: "글로벌",
}

type ThemePageProps = {
  params: {
    theme: string
  }
}

export default function ThemePage({ params }: ThemePageProps) {
  const { theme } = params

  // 유효한 테마인지 확인
  if (!etfData[theme as keyof typeof etfData]) {
    notFound()
  }

  const themeEtfs = etfData[theme as keyof typeof etfData]
  const themeName = themeNames[theme as keyof typeof themeNames]

  // 수익률 기준으로 정렬
  const sortedEtfs = [...themeEtfs].sort((a, b) => b.returnRate - a.returnRate)

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">{themeName} 테마 ETF</h1>
        <p className="text-slate-500">{themeName} 관련 ETF 목록입니다.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{themeName} ETF 랭킹</CardTitle>
          <CardDescription>수익률 기준으로 정렬된 {themeName} 테마 ETF 목록입니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>순위</TableHead>
                <TableHead>ETF명</TableHead>
                <TableHead>종목코드</TableHead>
                <TableHead className="text-right">현재가</TableHead>
                <TableHead className="text-right">등락률</TableHead>
                <TableHead className="text-right">수익률</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {sortedEtfs.map((etf, index) => (
                <TableRow key={etf.id} className="cursor-pointer hover:bg-slate-50">
                  <TableCell className="font-medium">{index + 1}</TableCell>
                  <TableCell>
                    <Link href={`/etf/${etf.ticker}`} className="hover:underline text-blue-600">
                      {etf.name}
                    </Link>
                  </TableCell>
                  <TableCell>{etf.ticker}</TableCell>
                  <TableCell className="text-right">{etf.price.toLocaleString()}원</TableCell>
                  <TableCell className={`text-right ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                    {etf.change >= 0 ? "+" : ""}
                    {etf.change}%
                  </TableCell>
                  <TableCell className="text-right font-bold text-green-600">+{etf.returnRate}%</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}
