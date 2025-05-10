import Link from "next/link"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ArrowLeft, Star, Share2, Bell } from "lucide-react"
import { ETFChart } from "@/components/etf-chart"
import { notFound } from "next/navigation"

// 샘플 ETF 데이터 객체에 누락된 ETF 데이터를 추가합니다.
// "266370" (KINDEX 필수소비재) 데이터를 추가합니다.
const etfData = {
  "005930": {
    id: 1,
    name: "KODEX 삼성전자",
    ticker: "005930",
    theme: "기술",
    returnRate: 28.5,
    price: 82500,
    change: 2.1,
    volume: 1250000,
    marketCap: 4120000000000,
    description: "삼성전자 주식에 투자하는 ETF로, 국내 대표 기술주에 투자할 수 있습니다.",
    nav: 82450,
    aum: 4120000000000,
    expense: 0.15,
    issuer: "삼성자산운용",
    launchDate: "2018-05-15",
    holdings: [{ name: "삼성전자", weight: 100 }],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [81200, 81500, 82100, 81900, 82300, 82100, 82500],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [80500, 81200, 80800, 81500, 82000, 81800, 82500],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [78500, 79200, 80500, 81200, 80800, 81500, 82500],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [64200, 65500, 68200, 70500, 72100, 73500, 75200, 76800, 78500, 79800, 81200, 82500],
      },
    },
  },
  "305720": {
    id: 2,
    name: "TIGER 2차전지",
    ticker: "305720",
    theme: "에너지",
    returnRate: 25.7,
    price: 42300,
    change: 1.8,
    volume: 980000,
    marketCap: 2150000000000,
    description: "2차전지 관련 기업에 투자하는 ETF로, 배터리 및 에너지 저장 기술 분야의 성장에 투자할 수 있습니다.",
    nav: 42250,
    aum: 2150000000000,
    expense: 0.18,
    issuer: "미래에셋자산운용",
    launchDate: "2019-08-23",
    holdings: [
      { name: "LG에너지솔루션", weight: 25 },
      { name: "삼성SDI", weight: 22 },
      { name: "SK이노베이션", weight: 18 },
      { name: "에코프로", weight: 15 },
      { name: "기타", weight: 20 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [41500, 42000, 41800, 42200, 42100, 42400, 42300],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [40800, 41200, 41500, 41800, 42100, 42000, 42300],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [38500, 39200, 40100, 40800, 41500, 41900, 42300],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [33600, 34500, 35800, 36500, 37200, 38500, 39200, 40100, 40800, 41500, 41900, 42300],
      },
    },
  },
  "244580": {
    id: 3,
    name: "KODEX 바이오",
    ticker: "244580",
    theme: "헬스케어",
    returnRate: 22.3,
    price: 15600,
    change: 0.9,
    volume: 750000,
    marketCap: 1850000000000,
    description: "국내 바이오 및 제약 기업에 투자하는 ETF로, 헬스케어 산업의 성장에 투자할 수 있습니다.",
    nav: 15580,
    aum: 1850000000000,
    expense: 0.17,
    issuer: "삼성자산운용",
    launchDate: "2017-03-10",
    holdings: [
      { name: "삼성바이오로직스", weight: 22 },
      { name: "셀트리온", weight: 18 },
      { name: "SK바이오사이언스", weight: 15 },
      { name: "한미약품", weight: 12 },
      { name: "기타", weight: 33 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [15400, 15450, 15500, 15550, 15580, 15620, 15600],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [15200, 15300, 15350, 15400, 15500, 15550, 15600],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [14800, 14950, 15100, 15250, 15400, 15500, 15600],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [12750, 13100, 13450, 13800, 14150, 14500, 14750, 15000, 15200, 15350, 15500, 15600],
      },
    },
  },
  "289480": {
    id: 4,
    name: "ARIRANG 글로벌4차산업",
    ticker: "289480",
    theme: "기술",
    returnRate: 21.2,
    price: 9850,
    change: 1.2,
    volume: 680000,
    marketCap: 1650000000000,
    description:
      "글로벌 4차 산업혁명 관련 기업에 투자하는 ETF로, AI, 로봇, 빅데이터 등 미래 기술 분야에 투자할 수 있습니다.",
    nav: 9830,
    aum: 1650000000000,
    expense: 0.19,
    issuer: "한화자산운용",
    launchDate: "2018-11-05",
    holdings: [
      { name: "NVIDIA", weight: 8 },
      { name: "Tesla", weight: 7 },
      { name: "Microsoft", weight: 6 },
      { name: "Amazon", weight: 5 },
      { name: "기타", weight: 74 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [9750, 9780, 9800, 9820, 9840, 9830, 9850],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [9650, 9700, 9720, 9750, 9780, 9820, 9850],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [9450, 9520, 9580, 9650, 9720, 9780, 9850],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [8120, 8350, 8580, 8750, 8950, 9120, 9280, 9420, 9550, 9650, 9750, 9850],
      },
    },
  },
  "133690": {
    id: 5,
    name: "TIGER 미국나스닥100",
    ticker: "133690",
    theme: "글로벌",
    returnRate: 20.8,
    price: 21500,
    change: 0.5,
    volume: 920000,
    marketCap: 3250000000000,
    description: "미국 나스닥100 지수를 추종하는 ETF로, 미국 기술주 중심의 글로벌 투자가 가능합니다.",
    nav: 21480,
    aum: 3250000000000,
    expense: 0.16,
    issuer: "미래에셋자산운용",
    launchDate: "2016-04-18",
    holdings: [
      { name: "Apple", weight: 12 },
      { name: "Microsoft", weight: 10 },
      { name: "Amazon", weight: 8 },
      { name: "Alphabet", weight: 7 },
      { name: "기타", weight: 63 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [21400, 21450, 21480, 21460, 21490, 21510, 21500],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [21300, 21350, 21380, 21420, 21450, 21480, 21500],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [20900, 21050, 21150, 21250, 21350, 21450, 21500],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [17800, 18200, 18600, 19000, 19400, 19800, 20200, 20500, 20800, 21100, 21300, 21500],
      },
    },
  },
  "091170": {
    id: 6,
    name: "KODEX 은행",
    ticker: "091170",
    theme: "금융",
    returnRate: 18.9,
    price: 11200,
    change: -0.3,
    volume: 520000,
    marketCap: 1420000000000,
    description: "국내 주요 은행 및 금융 기업에 투자하는 ETF로, 금융 산업의 성장에 투자할 수 있습니다.",
    nav: 11180,
    aum: 1420000000000,
    expense: 0.16,
    issuer: "삼성자산운용",
    launchDate: "2016-09-22",
    holdings: [
      { name: "KB금융", weight: 22 },
      { name: "신한지주", weight: 20 },
      { name: "하나금융지주", weight: 18 },
      { name: "우리금융지주", weight: 15 },
      { name: "기타", weight: 25 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [11250, 11230, 11210, 11220, 11190, 11210, 11200],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [11300, 11280, 11250, 11240, 11220, 11210, 11200],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [11050, 11100, 11150, 11200, 11250, 11230, 11200],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [9400, 9600, 9800, 10000, 10200, 10400, 10600, 10800, 11000, 11100, 11150, 11200],
      },
    },
  },
  "371460": {
    id: 7,
    name: "TIGER 차이나전기차",
    ticker: "371460",
    theme: "에너지",
    returnRate: 17.5,
    price: 8750,
    change: -0.8,
    volume: 480000,
    marketCap: 980000000000,
    description: "중국 전기차 관련 기업에 투자하는 ETF로, 중국 전기차 산업의 성장에 투자할 수 있습니다.",
    nav: 8730,
    aum: 980000000000,
    expense: 0.19,
    issuer: "미래에셋자산운용",
    launchDate: "2020-02-14",
    holdings: [
      { name: "BYD", weight: 18 },
      { name: "NIO", weight: 15 },
      { name: "CATL", weight: 14 },
      { name: "XPeng", weight: 12 },
      { name: "기타", weight: 41 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [8820, 8800, 8780, 8760, 8770, 8760, 8750],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [8850, 8830, 8810, 8790, 8780, 8770, 8750],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [8650, 8700, 8750, 8800, 8820, 8780, 8750],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [7450, 7600, 7750, 7900, 8050, 8200, 8350, 8500, 8650, 8700, 8720, 8750],
      },
    },
  },
  "266370": {
    id: 8,
    name: "KINDEX 필수소비재",
    ticker: "266370",
    theme: "소비재",
    returnRate: 15.2,
    price: 13400,
    change: 0.2,
    volume: 350000,
    marketCap: 1120000000000,
    description:
      "필수소비재 관련 기업에 투자하는 ETF로, 경기 변동에 상대적으로 안정적인 소비재 산업에 투자할 수 있습니다.",
    nav: 13380,
    aum: 1120000000000,
    expense: 0.15,
    issuer: "한국투자신탁운용",
    launchDate: "2017-11-08",
    holdings: [
      { name: "LG생활건강", weight: 15 },
      { name: "아모레퍼시픽", weight: 12 },
      { name: "CJ제일제당", weight: 10 },
      { name: "오리온", weight: 8 },
      { name: "기타", weight: 55 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [13380, 13390, 13400, 13410, 13400, 13410, 13400],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [13350, 13360, 13370, 13380, 13390, 13400, 13400],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [13200, 13250, 13300, 13320, 13350, 13380, 13400],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [11650, 11850, 12050, 12250, 12450, 12650, 12850, 13050, 13200, 13300, 13350, 13400],
      },
    },
  },
  "365040": {
    id: 9,
    name: "TIGER IT테크",
    ticker: "365040",
    theme: "기술",
    returnRate: 14.8,
    price: 12700,
    change: 0.7,
    volume: 420000,
    marketCap: 1350000000000,
    description:
      "국내 IT 기술 기업에 투자하는 ETF로, 소프트웨어, 하드웨어, 인터넷 서비스 등 IT 산업 전반에 투자할 수 있습니다.",
    nav: 12680,
    aum: 1350000000000,
    expense: 0.17,
    issuer: "미래에셋자산운용",
    launchDate: "2019-12-05",
    holdings: [
      { name: "삼성전자", weight: 20 },
      { name: "SK하이닉스", weight: 15 },
      { name: "네이버", weight: 12 },
      { name: "카카오", weight: 10 },
      { name: "기타", weight: 43 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [12610, 12640, 12660, 12680, 12690, 12710, 12700],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [12550, 12580, 12610, 12640, 12670, 12690, 12700],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [12400, 12450, 12500, 12550, 12600, 12650, 12700],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [11050, 11200, 11350, 11500, 11650, 11800, 11950, 12100, 12250, 12400, 12550, 12700],
      },
    },
  },
  "272560": {
    id: 10,
    name: "KINDEX 금융",
    ticker: "272560",
    theme: "금융",
    returnRate: 12.5,
    price: 9800,
    change: -0.5,
    volume: 310000,
    marketCap: 950000000000,
    description: "국내 금융 기업에 투자하는 ETF로, 은행, 보험, 증권 등 금융 산업 전반에 투자할 수 있습니다.",
    nav: 9780,
    aum: 950000000000,
    expense: 0.16,
    issuer: "한국투자신탁운용",
    launchDate: "2018-01-15",
    holdings: [
      { name: "KB금융", weight: 18 },
      { name: "신한지주", weight: 16 },
      { name: "삼성생명", weight: 14 },
      { name: "미래에셋증권", weight: 12 },
      { name: "기타", weight: 40 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [9850, 9840, 9830, 9820, 9810, 9800, 9800],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [9880, 9870, 9860, 9850, 9830, 9810, 9800],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [9700, 9730, 9760, 9790, 9820, 9810, 9800],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [8700, 8800, 8900, 9000, 9100, 9200, 9300, 9400, 9500, 9600, 9700, 9800],
      },
    },
  },
  "227540": {
    id: 11,
    name: "TIGER 헬스케어",
    ticker: "227540",
    theme: "헬스케어",
    returnRate: 11.2,
    price: 13400,
    change: 0.3,
    volume: 290000,
    marketCap: 1050000000000,
    description:
      "국내 헬스케어 기업에 투자하는 ETF로, 제약, 바이오, 의료기기 등 헬스케어 산업 전반에 투자할 수 있습니다.",
    nav: 13380,
    aum: 1050000000000,
    expense: 0.17,
    issuer: "미래에셋자산운용",
    launchDate: "2017-05-22",
    holdings: [
      { name: "삼성바이오로직스", weight: 20 },
      { name: "셀트리온", weight: 18 },
      { name: "유한양행", weight: 12 },
      { name: "녹십자", weight: 10 },
      { name: "기타", weight: 40 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [13360, 13370, 13380, 13390, 13400, 13410, 13400],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [13320, 13340, 13350, 13360, 13380, 13390, 13400],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [13200, 13240, 13280, 13320, 13350, 13380, 13400],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [12050, 12150, 12250, 12350, 12550, 12750, 12950, 13050, 13150, 13250, 13350, 13400],
      },
    },
  },
  "266410": {
    id: 12,
    name: "KODEX 소비자재",
    ticker: "266410",
    theme: "소비재",
    returnRate: 10.8,
    price: 10200,
    change: -0.1,
    volume: 270000,
    marketCap: 920000000000,
    description: "소비자재 관련 기업에 투자하는 ETF로, 유통, 의류, 가전 등 소비재 산업 전반에 투자할 수 있습니다.",
    nav: 10190,
    aum: 920000000000,
    expense: 0.15,
    issuer: "삼성자산운용",
    launchDate: "2017-11-10",
    holdings: [
      { name: "삼성전자", weight: 15 },
      { name: "현대차", weight: 12 },
      { name: "LG전자", weight: 10 },
      { name: "롯데쇼핑", weight: 8 },
      { name: "기타", weight: 55 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [10210, 10210, 10200, 10210, 10200, 10210, 10200],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [10220, 10220, 10210, 10210, 10200, 10210, 10200],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [10150, 10160, 10170, 10180, 10190, 10200, 10200],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [9200, 9300, 9400, 9500, 9600, 9700, 9800, 9900, 10000, 10100, 10150, 10200],
      },
    },
  },
  "251350": {
    id: 13,
    name: "KODEX 선진국MSCI",
    ticker: "251350",
    theme: "글로벌",
    returnRate: 9.5,
    price: 11800,
    change: 0.2,
    volume: 250000,
    marketCap: 880000000000,
    description: "선진국 MSCI 지수를 추종하는 ETF로, 미국, 유럽, 일본 등 선진국 시장에 분산 투자할 수 있습니다.",
    nav: 11780,
    aum: 880000000000,
    expense: 0.16,
    issuer: "삼성자산운용",
    launchDate: "2017-08-03",
    holdings: [
      { name: "Apple", weight: 5 },
      { name: "Microsoft", weight: 4 },
      { name: "Amazon", weight: 3 },
      { name: "Toyota", weight: 2 },
      { name: "기타", weight: 86 },
    ],
    chartData: {
      daily: {
        labels: ["9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00"],
        values: [11780, 11790, 11780, 11790, 11800, 11790, 11800],
      },
      weekly: {
        labels: ["월", "화", "수", "목", "금", "월", "화"],
        values: [11760, 11770, 11780, 11780, 11790, 11790, 11800],
      },
      monthly: {
        labels: ["1주", "2주", "3주", "4주", "1주", "2주", "3주"],
        values: [11700, 11720, 11740, 11760, 11770, 11790, 11800],
      },
      yearly: {
        labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
        values: [10780, 10880, 10980, 11080, 11180, 11280, 11380, 11480, 11580, 11680, 11750, 11800],
      },
    },
  },
}

// 관련 ETF 데이터 매핑에 누락된 ETF 데이터를 추가합니다.
const relatedEtfsMap = {
  "005930": [
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
  "305720": [
    {
      id: 7,
      name: "TIGER 차이나전기차",
      ticker: "371460",
      theme: "에너지",
      returnRate: 17.5,
      price: 8750,
      change: -0.8,
    },
    {
      id: 14,
      name: "KODEX 에너지",
      ticker: "117680",
      theme: "에너지",
      returnRate: 16.2,
      price: 14500,
      change: 0.4,
    },
  ],
  "244580": [
    {
      id: 11,
      name: "TIGER 헬스케어",
      ticker: "227540",
      theme: "헬스케어",
      returnRate: 11.2,
      price: 13400,
      change: 0.3,
    },
    {
      id: 15,
      name: "KODEX 글로벌헬스케어",
      ticker: "266420",
      theme: "헬스케어",
      returnRate: 18.5,
      price: 16800,
      change: 0.6,
    },
  ],
  "289480": [
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
      id: 9,
      name: "TIGER IT테크",
      ticker: "365040",
      theme: "기술",
      returnRate: 14.8,
      price: 12700,
      change: 0.7,
    },
  ],
  "133690": [
    {
      id: 16,
      name: "KODEX 선진국MSCI",
      ticker: "251350",
      theme: "글로벌",
      returnRate: 9.5,
      price: 11800,
      change: 0.2,
    },
    {
      id: 17,
      name: "TIGER S&P500",
      ticker: "360750",
      theme: "글로벌",
      returnRate: 15.3,
      price: 18500,
      change: 0.4,
    },
  ],
  "091170": [
    {
      id: 10,
      name: "KINDEX 금융",
      ticker: "272560",
      theme: "금융",
      returnRate: 12.5,
      price: 9800,
      change: -0.5,
    },
    {
      id: 18,
      name: "KODEX 증권",
      ticker: "102970",
      theme: "금융",
      returnRate: 14.2,
      price: 10500,
      change: -0.2,
    },
  ],
  "371460": [
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
      id: 14,
      name: "KODEX 에너지",
      ticker: "117680",
      theme: "에너지",
      returnRate: 16.2,
      price: 14500,
      change: 0.4,
    },
  ],
  "266370": [
    {
      id: 12,
      name: "KODEX 소비자재",
      ticker: "266410",
      theme: "소비재",
      returnRate: 10.8,
      price: 10200,
      change: -0.1,
    },
    {
      id: 19,
      name: "TIGER 소비재",
      ticker: "227560",
      theme: "소비재",
      returnRate: 11.5,
      price: 12100,
      change: 0.3,
    },
  ],
  "365040": [
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
  ],
  "272560": [
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
      id: 18,
      name: "KODEX 증권",
      ticker: "102970",
      theme: "금융",
      returnRate: 14.2,
      price: 10500,
      change: -0.2,
    },
  ],
  "227540": [
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
      id: 15,
      name: "KODEX 글로벌헬스케어",
      ticker: "266420",
      theme: "헬스케어",
      returnRate: 18.5,
      price: 16800,
      change: 0.6,
    },
  ],
  "266410": [
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
      id: 19,
      name: "TIGER 소비재",
      ticker: "227560",
      theme: "소비재",
      returnRate: 11.5,
      price: 12100,
      change: 0.3,
    },
  ],
  "251350": [
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
      id: 17,
      name: "TIGER S&P500",
      ticker: "360750",
      theme: "글로벌",
      returnRate: 15.3,
      price: 18500,
      change: 0.4,
    },
  ],
}

export default function ETFDetailPage({ params }: { params: { ticker: string } }) {
  const { ticker } = params
  const etf = etfData[ticker as keyof typeof etfData]

  if (!etf) {
    notFound()
  }

  const relatedEtfs = relatedEtfsMap[ticker as keyof typeof relatedEtfsMap] || []

  return (
    <div className="container mx-auto py-6 px-4">
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
            <Button variant="outline" size="icon">
              <Star className="h-4 w-4" />
            </Button>
            <Button variant="outline" size="icon">
              <Bell className="h-4 w-4" />
            </Button>
            <Button variant="outline" size="icon">
              <Share2 className="h-4 w-4" />
            </Button>
            <Button>매수하기</Button>
          </div>
        </div>
      </div>

      <div className="grid md:grid-cols-3 gap-6 mb-8">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle>현재가</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-end gap-2">
              <div className="text-3xl font-bold">{etf.price.toLocaleString()}원</div>
              <div className={`text-lg ${etf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                {etf.change >= 0 ? "+" : ""}
                {etf.change}%
              </div>
            </div>
            <p className="text-sm text-slate-500 mt-1">NAV: {etf.nav.toLocaleString()}원</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle>수익률</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold text-green-600">+{etf.returnRate}%</div>
            <p className="text-sm text-slate-500 mt-1">최근 1년 기준</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle>거래량</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">{etf.volume.toLocaleString()}</div>
            <p className="text-sm text-slate-500 mt-1">오늘 기준</p>
          </CardContent>
        </Card>
      </div>

      <div className="mb-8">
        <Tabs defaultValue="daily">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-2xl font-bold">가격 차트</h2>
            <TabsList>
              <TabsTrigger value="daily">일간</TabsTrigger>
              <TabsTrigger value="weekly">주간</TabsTrigger>
              <TabsTrigger value="monthly">월간</TabsTrigger>
              <TabsTrigger value="yearly">연간</TabsTrigger>
            </TabsList>
          </div>

          <TabsContent value="daily">
            <ETFChart data={etf.chartData.daily} title="일간 차트" />
          </TabsContent>

          <TabsContent value="weekly">
            <ETFChart data={etf.chartData.weekly} title="주간 차트" />
          </TabsContent>

          <TabsContent value="monthly">
            <ETFChart data={etf.chartData.monthly} title="월간 차트" />
          </TabsContent>

          <TabsContent value="yearly">
            <ETFChart data={etf.chartData.yearly} title="연간 차트" color={etf.change >= 0 ? "#22c55e" : "#ef4444"} />
          </TabsContent>
        </Tabs>
      </div>

      <div className="grid md:grid-cols-2 gap-6 mb-8">
        <Card>
          <CardHeader>
            <CardTitle>ETF 정보</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <p>{etf.description}</p>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-slate-500">운용사</p>
                  <p className="font-medium">{etf.issuer}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">설정일</p>
                  <p className="font-medium">{etf.launchDate}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">총 자산</p>
                  <p className="font-medium">{(etf.aum / 1000000000000).toFixed(2)}조원</p>
                </div>
                <div>
                  <p className="text-sm text-slate-500">보수율</p>
                  <p className="font-medium">{etf.expense}%</p>
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
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>종목명</TableHead>
                  <TableHead className="text-right">비중</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {etf.holdings.map((holding, index) => (
                  <TableRow key={index}>
                    <TableCell>{holding.name}</TableCell>
                    <TableCell className="text-right">{holding.weight}%</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      </div>

      <div className="mb-8">
        <h2 className="text-2xl font-bold mb-4">관련 ETF</h2>
        <div className="grid md:grid-cols-2 gap-4">
          {relatedEtfs.map((relatedEtf) => (
            <Link href={`/etf/${relatedEtf.ticker}`} key={relatedEtf.id}>
              <Card className="hover:shadow-md transition-shadow cursor-pointer">
                <CardContent className="p-4">
                  <div className="flex justify-between items-center">
                    <div>
                      <h3 className="font-semibold">{relatedEtf.name}</h3>
                      <p className="text-sm text-slate-500">
                        {relatedEtf.ticker} | {relatedEtf.theme}
                      </p>
                    </div>
                    <div className="text-right">
                      <div className={`font-bold ${relatedEtf.change >= 0 ? "text-green-600" : "text-red-600"}`}>
                        {relatedEtf.change >= 0 ? "+" : ""}
                        {relatedEtf.change}%
                      </div>
                      <div className="text-sm">{relatedEtf.price.toLocaleString()}원</div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
