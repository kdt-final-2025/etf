
import { Suspense } from 'react'
import Link from 'next/link'
import { notFound } from 'next/navigation'
import { ArrowLeft, ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import EtfFilters from './EtfFilters'

// ETF 응답 타입 정의
interface EtfItem {
    etfName: string
    etfCode: string
    theme: string
    returnRate: number
}

interface EtfResponse {
    totalPage: number
    totalCount: number
    currentPage: number
    pageSize: number
    etfReadResponseList: EtfItem[]
}

// 유효 테마 리스트
const validThemes = [
    "AI_DATA", "USA", "KOREA", "REITS", "MULTI_ASSET", "COMMODITIES",
    "HIGH_RISK", "SECTOR", "DIVIDEND", "ESG", "GOLD",
    "GOVERNMENT_BOND", "CORPORATE_BOND", "DEFENSE", "SEMICONDUCTOR",
    "BIO", "EMERGING_MARKETS"
]

// 테마 이름 표시용
function getThemeDisplayName(themeId: string): string {
    const themeMap: Record<string, string> = {
        AI_DATA: 'AI 데이터',
        USA: '미국',
        KOREA: '한국',
        REITS: '리츠',
        MULTI_ASSET: '멀티에셋',
        COMMODITIES: '원자재',
        HIGH_RISK: '고위험',
        SECTOR: '섹터',
        DIVIDEND: '배당',
        ESG: 'ESG',
        GOLD: '금',
        GOVERNMENT_BOND: '국채',
        CORPORATE_BOND: '회사채',
        DEFENSE: '방위산업',
        SEMICONDUCTOR: '반도체',
        BIO: '바이오',
        EMERGING_MARKETS: '신흥시장',
    }
    return themeMap[themeId] || themeId
}

// 서버에서 ETF 데이터 패칭
async function fetchEtfsByTheme(
    theme: string,
    page: number = 1,
    size: number = 20,
    keyword: string = ''
): Promise<EtfResponse> {
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'
    const apiUrl = `${baseUrl}/api/v1/etfs?theme=${theme}&page=${page}&size=${size}&period=weekly&keyword=${encodeURIComponent(keyword)}`

    try {
        const res = await fetch(apiUrl, { next: { revalidate: 3600 } })
        if (!res.ok) throw new Error(`API 요청 실패: ${res.status}`)
        return await res.json()
    } catch (e) {
        console.error('ETF 데이터 가져오기 실패:', e)
        return {
            totalPage: 0,
            totalCount: 0,
            currentPage: 1,
            pageSize: 20,
            etfReadResponseList: []
        }
    }
}

// ETF 테이블 렌더러
function EtfTable({ etfs }: { etfs: EtfItem[] }) {
    return (
        <div className="overflow-x-auto rounded-lg border border-gray-200">
            <table className="w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ETF 이름</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">종목코드</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">수익률 (%)</th>
                </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                {etfs.length === 0 ? (
                    <tr>
                        <td colSpan={3} className="px-6 py-4 text-center text-sm text-gray-500">
                            ETF 정보가 없습니다
                        </td>
                    </tr>
                ) : etfs.map(etf => (
                    <tr key={etf.etfCode} className="hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm font-medium text-gray-900">{etf.etfName}</td>
                        <td className="px-6 py-4 text-sm text-gray-500">{etf.etfCode}</td>
                        <td className={`px-6 py-4 text-sm text-right font-medium ${
                            etf.returnRate > 0 ? 'text-red-600' :
                                etf.returnRate < 0 ? 'text-blue-600' :
                                    'text-gray-500'
                        }`}>
                            {etf.returnRate > 0 ? '+' : ''}{etf.returnRate.toFixed(2)}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    )
}

// 페이지네이션
function Pagination({
                        theme,
                        currentPage,
                        totalPages,
                        keyword
                    }: {
    theme: string
    currentPage: number
    totalPages: number
    keyword: string
}) {
    if (totalPages <= 1) return null

    const prevPage = Math.max(1, currentPage - 1)
    const nextPage = Math.min(totalPages, currentPage + 1)

    return (
        <div className="flex items-center justify-center mt-6 gap-1">
            <Link
                href={`/themes/${theme}?page=${prevPage}&keyword=${encodeURIComponent(keyword)}`}
                className={currentPage <= 1 ? 'pointer-events-none opacity-50' : ''}
            >
                <Button variant="outline" size="sm" disabled={currentPage <= 1}>
                    <ArrowLeft className="h-4 w-4 mr-1" /> 이전
                </Button>
            </Link>

            <span className="px-3 py-1 text-sm bg-gray-100 rounded-md">
        {currentPage} / {totalPages}
      </span>

            <Link
                href={`/themes/${theme}?page=${nextPage}&keyword=${encodeURIComponent(keyword)}`}
                className={currentPage >= totalPages ? 'pointer-events-none opacity-50' : ''}
            >
                <Button variant="outline" size="sm" disabled={currentPage >= totalPages}>
                    다음 <ArrowRight className="h-4 w-4 ml-1" />
                </Button>
            </Link>
        </div>
    )
}

// 메인 페이지 컴포넌트
export default async function ThemePage({
                                            params,
                                            searchParams
                                        }: {
    params: Promise<{ theme: string }>
    searchParams: Promise<{
        page?: string
        size?: string
        keyword?: string
    }>
}) {
    // params 비동기 해제
    const { theme } = await params

    // searchParams 비동기 해제
    const {
        page: rawPage = '1',
        size: rawSize = '20',
        keyword = ''
    } = await searchParams

    // 2) 숫자 변환
    const page = parseInt(rawPage, 10)
    const size = parseInt(rawSize, 10)

    // 3) 유효 테마 체크
    if (!validThemes.includes(theme)) {
        notFound()
    }

    // 4) 데이터 패칭
    const etfData = await fetchEtfsByTheme(theme, page, size, keyword)

    return (
        <div className="container mx-auto px-4 py-8">
            <header className="mb-8">
                <h1 className="text-3xl font-bold">{getThemeDisplayName(theme)} 테마 ETF</h1>
                <p className="mt-2 text-gray-500">
                    총 <span className="font-medium">{etfData.totalCount.toLocaleString()}</span>개의 ETF가 있습니다
                </p>
            </header>

            <div className="mb-6">
                <Suspense fallback={<div>필터 로딩중...</div>}>
                    <EtfFilters theme={theme} initialKeyword={keyword} />
                </Suspense>
            </div>

            <EtfTable etfs={etfData.etfReadResponseList} />

            {etfData.totalPage > 0 && (
                <Pagination
                    theme={theme}
                    currentPage={etfData.currentPage}
                    totalPages={etfData.totalPage}
                    keyword={keyword}
                />
            )}
        </div>
    )
}
