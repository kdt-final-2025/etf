// app/etf/[id]/page.tsx
'use client'

import React, { useEffect, useState } from 'react'
import { useParams } from 'next/navigation'
import Link from 'next/link'
import { ArrowLeft, Star, Bell, Share2 } from 'lucide-react'
import TradingViewWidget from '@/components/tradingViewWidget'

import {createComment, deleteComment, updateComment} from './actions'

import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import { Table, TableHeader, TableHead, TableBody, TableRow, TableCell } from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { ETFChart } from '@/components/etf-chart'
import {getCommentsByEtfId} from "@/app/etf/[id]/actions";

// ─── 타입 정의 ─────────────────────────────────────
type Holding = { name: string; weight: number }

type ETF = {
    id: number
    name: string
    ticker: string
    issuer: string
    theme: string
    description: string
    price: number
    nav: number
    change: number
    returnRate: number
    volume: number
    launchDate: string
    aum: number
    expense: number
    holdings: Holding[]
    chartData: {
        daily: any[]
        weekly: any[]
        monthly: any[]
        yearly: any[]
    }
}

type CommentResponse = {
    id: number
    userId: number
    nickName: string
    imageUrl: string
    content: string
    likesCount: number
    createdAt: string
}

// 백엔드가 반환하는 페이지 객체
type CommentsPageList = {
    page: number
    size: number
    totalElements: number
    totalPages: number
    etfId: number
    commentResponses: CommentResponse[]
}

export default function ETFDetailPage() {
    // URL param에서 id
    const { id } = useParams()


    // 상태
    const [etf, setEtf] = useState<ETF | null>(null)
    const [comments, setComments] = useState<CommentResponse[]>([])
    const [newComment, setNewComment] = useState('')
    const [accessToken, setAccessToken] = useState<string>('');
    const params = useParams()
    const [loading, setLoading] = useState(true)
    const [editMode, setEditMode] = useState(false); // 수정 모드 상태
    const [editedContent, setEditedContent] = useState(''); // 수정된 내용
    const [editingCommentId, setEditingCommentId] = useState(null); // 수정 중인 댓글의 ID

    const etfId = Number(params.id)
    // 클라이언트에서만 접근 가능한 토큰
    const token =
        typeof window !== 'undefined'
            ? localStorage.getItem('accessToken')
            : null
    // 수정 버튼 클릭 시
    const handleEditClick = (commentId, currentContent) => {
        setEditingCommentId(commentId);
        setEditedContent(currentContent); // 현재 댓글 내용을 수정할 텍스트로 설정
        setEditMode(true); // 수정 모드 활성화
    };

    // 수정 취소 버튼 클릭 시
    const handleCancelEdit = () => {
        setEditMode(false); // 수정 모드 종료
        setEditedContent(''); // 입력 필드 초기화
        setEditingCommentId(null); // 수정 중인 댓글 ID 초기화
    };

    // 수정 내용 제출 시
    const handleSubmit = async () => {
        if (editedContent.trim() === '') {
            alert('댓글 내용은 비워둘 수 없습니다!');
            return;
        }

        try {
            // 서버로 수정된 내용 전달
            await updateComment(editingCommentId, { content: editedContent });

            // 수정 완료 후 상태 초기화
            setEditMode(false);
            setEditingCommentId(null);
            setEditedContent('');
        } catch (error) {
            console.error('댓글 수정 오류:', error);
            alert('댓글 수정에 실패했습니다. 다시 시도해 주세요.');
        }
    };
    // ─── 1) ETF 상세 정보 로드 ─────────────────────────
    useEffect(() => {
        if (!etfId) return

        fetch(`http://localhost:8080/api/v1/etfs/${etfId}`, { cache: 'no-store' })
            .then(res => {
                if (!res.ok) throw new Error('ETF not found')
                return res.json()
            })
            .then(data => {
                setEtf({
                    id: data.etfId,
                    name: data.etfName,
                    ticker: data.etfCode,
                    issuer: data.companyName,
                    theme: '',
                    description: '',
                    price: 0,
                    nav: 0,
                    change: 0,
                    returnRate: 0,
                    volume: 0,
                    launchDate: data.listingDate,
                    aum: 0,
                    expense: 0,
                    holdings: [],
                    chartData: { daily: [], weekly: [], monthly: [], yearly: [] },
                })
            })
            .catch(err => console.error('ETF load error:', err))
    }, [etfId])

    // ─── 2) 댓글 목록 로드 ────────────────────────────

    useEffect(() => {
        if (!etf) return

        // comments를 getCommentsByEtfId에서 가져옴
        getCommentsByEtfId(etf.id).then((data) => {
            if (data && Array.isArray(data)) {
                setComments(data) // 정상 데이터가 있으면 업데이트
            } else {
                setComments([]) // null 또는 빈 데이터면 빈 배열로 설정
            }
        }).catch(err => console.error('Error loading comments:', err))
    }, [etf])

    // ─── 3) 댓글 작성 (서버 액션) ────────────────────
    // <form action={createComment}> 방식으로 처리하면 자동 페이지 리프레시됩니다.

    // 로딩 처리
    if (!etf) {
        return (
            <div className="p-6 text-center text-gray-500">
                ETF 데이터를 불러오는 중입니다...
            </div>
        )
    }

    const handleDelete = async (commentId: number) => {
        try {
            setLoading(true);

            // 댓글 삭제 처리
            const success = await deleteComment(commentId, accessToken);

            if (success) {
                // 댓글 삭제 성공 시 해당 댓글을 상태에서 제거
                setComments(prevComments =>
                    prevComments.filter(comment => comment.id !== commentId)
                );
            }
        } catch (error) {
            console.error('댓글 삭제 실패:', error);
        } finally {
            setLoading(false);
        }
    };


    return (
        <div className="container mx-auto py-6 px-4">
            {/* 뒤로가기 & 헤더 */}
            <Link
                href="/"
                className="flex items-center gap-1 text-slate-500 hover:text-slate-700 mb-6"
            >
                <ArrowLeft className="h-4 w-4" />
                <span>홈으로 돌아가기</span>
            </Link>

            {/* ─── 헤더: 이름·티커·운용사·버튼 ───────────────── */}
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-8">
                <div>
                    <div className="flex items-center gap-2">
                        <h1 className="text-3xl font-bold">{etf.name}</h1>
                        <Badge variant="outline">{etf.theme || '—'}</Badge>
                    </div>
                    <p className="text-slate-500">
                        {etf.ticker.toUpperCase()} | {etf.issuer}
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

            {/* ─── 주요 지표 카드 ───────────────────────────── */}
            <div className="grid md:grid-cols-3 gap-6 mb-8">
                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle>현재가</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="flex items-end gap-2">
                            <div className="text-3xl font-bold">
                                {etf.price.toLocaleString()}원
                            </div>
                            <div
                                className={`text-lg ${
                                    etf.change >= 0 ? 'text-green-600' : 'text-red-600'
                                }`}
                            >
                                {etf.change >= 0 ? '+' : ''}
                                {etf.change}%
                            </div>
                        </div>
                        <p className="text-sm text-slate-500 mt-1">
                            NAV: {etf.nav.toLocaleString()}원
                        </p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle>수익률</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold text-green-600">
                            +{etf.returnRate}%
                        </div>
                        <p className="text-sm text-slate-500 mt-1">최근 1년 기준</p>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="pb-2">
                        <CardTitle>거래량</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-bold">
                            {etf.volume.toLocaleString()}
                        </div>
                        <p className="text-sm text-slate-500 mt-1">오늘 기준</p>
                    </CardContent>
                </Card>
            </div>

            {/* ─── 가격 차트 ───────────────────────────────── */}
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

                    <div className="w-full h-[500px] mb-4">
                        <TradingViewWidget
                            symbol={`KRX:${etf.ticker.toUpperCase()}`}
                        />
                    </div>

                    <TabsContent value="daily">
                        {/*<ETFChart data={etf.chartData.daily} title="일간 차트" />*/}
                    </TabsContent>
                    <TabsContent value="weekly">
                        {/*<ETFChart data={etf.chartData.weekly} title="주간 차트" />*/}
                    </TabsContent>
                    <TabsContent value="monthly">
                        {/*<ETFChart data={etf.chartData.monthly} title="월간 차트" />*/}
                    </TabsContent>
                    <TabsContent value="yearly">
                        {/*<ETFChart data={etf.chartData.yearly} title="연간 차트" />*/}
                    </TabsContent>
                </Tabs>
            </div>

            {/* ─── ETF 정보 & 구성 종목 ───────────────────────── */}
            <div className="grid md:grid-cols-2 gap-6 mb-8">
                <Card>
                    <CardHeader>
                        <CardTitle>ETF 정보</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="mb-4">
                            {etf.description || '상세 정보가 없습니다.'}
                        </p>
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
                                <p className="font-medium">
                                    {(etf.aum / 1e12).toFixed(2)}조원
                                </p>
                            </div>
                            <div>
                                <p className="text-sm text-slate-500">보수율</p>
                                <p className="font-medium">{etf.expense}%</p>
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
                                    {etf.holdings.map((h, i) => (
                                        <TableRow key={i}>
                                            <TableCell>{h.name}</TableCell>
                                            <TableCell className="text-right">
                                                {h.weight}%
                                            </TableCell>
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

            {/* ─── 댓글 섹션 ───────────────────────────────── */}
            <section>
                <h2 className="text-2xl font-bold mb-4">댓글</h2>

                {/* 댓글 작성 폼 (서버 액션 사용) */}
                <form
                    action={createComment}
                    className="flex flex-col space-y-2 mb-6"
                >
          <textarea
              name="content"
              placeholder="댓글을 입력하세요."
              required
              className="p-2 border rounded focus:outline-none focus:ring"
          />
                    <input type="hidden" name="etfId" value={etf.id} />
                    <button
                        type="submit"
                        className="self-end px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                    >
                        댓글 작성
                    </button>
                </form>

                {/* 댓글 목록 */}
                {comments.length === 0 ? (
                    <p className="text-center text-gray-500">
                        등록된 댓글이 없습니다.
                    </p>
                ) : (
                    <ul className="space-y-4">
                        {comments.map((comment) => (
                            <li key={comment.id} className="bg-white p-4 rounded-lg shadow">
                                <div className="flex items-center justify-between">
                                    <div className="flex items-center gap-2">
                                        {comment.imageUrl && (
                                            <img
                                                src={comment.imageUrl || '/default-avatar.png'}  // 기본 이미지 URL로 대체
                                                alt="user"
                                                className="w-8 h-8 rounded-full"
                                                onError={(e) => e.target.src = '/default-avatar.png'} // 이미지 로드 오류 시 기본 이미지로 대체
                                            />
                                        )}
                                        <span className="font-semibold">{comment.nickName}</span>
                                    </div>
                                    <span className="text-sm text-gray-400">{new Date(comment.createdAt).toLocaleString()}</span>
                                </div>
                                {/* 수정 전 댓글 내용 */}
                                {!editMode || editingCommentId !== comment.id ? (
                                    <p className="mt-2 text-gray-800">{comment.content}</p>
                                ) : (
                                    // 수정 중일 때 텍스트 영역 보여주기
                                    <textarea
                                        value={editedContent}
                                        onChange={(e) => setEditedContent(e.target.value)}
                                        className="mt-2 w-full p-2 border border-gray-300 rounded"
                                    />
                                )}

                                {/* 수정 버튼과 취소 버튼 */}
                                {editMode && editingCommentId === comment.id ? (
                                    <div className="flex gap-2 mt-2">
                                        <button onClick={handleSubmit} className="bg-blue-500 text-white p-2 rounded">저장</button>
                                        <button onClick={handleCancelEdit} className="bg-gray-300 p-2 rounded">취소</button>
                                    </div>
                                ) : (
                                    <button onClick={() => handleEditClick(comment.id, comment.content)} className="bg-yellow-500 text-white p-2 rounded">수정</button>
                                )}

                                {/* 삭제 버튼 */}
                                <button
                                    onClick={() => handleDelete(comment.id)}
                                    className="bg-red-500 text-white p-2 rounded mt-2"
                                >
                                    삭제
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </div>
    )
}
