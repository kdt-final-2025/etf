"use client"

import type React from "react"

import { useState, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Switch } from "@/components/ui/switch"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Camera, Pencil } from "lucide-react"

export default function ProfilePage() {
  // 상태 관리
  const [nickname, setNickname] = useState("홍길동")
  const [userId, setUserId] = useState("hongildong") // 이메일 대신 아이디로 변경
  const [isPublicPortfolio, setIsPublicPortfolio] = useState(false)
  const [avatarSrc, setAvatarSrc] = useState("/placeholder.svg?height=96&width=96")
  const [isPasswordDialogOpen, setIsPasswordDialogOpen] = useState(false)
  const [currentPassword, setCurrentPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const fileInputRef = useRef<HTMLInputElement>(null)

  // 프로필 사진 변경 처리
  const handleAvatarClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => {
        if (event.target?.result) {
          setAvatarSrc(event.target.result.toString())
          alert("프로필 사진이 업데이트되었습니다.")
        }
      }
      reader.readAsDataURL(file)
    }
  }

  // 정보 수정 처리
  const handleProfileUpdate = () => {
    alert("프로필 정보가 업데이트되었습니다.")
  }

  // 비밀번호 변경 처리
  const handlePasswordChange = () => {
    if (currentPassword === "") {
      alert("현재 비밀번호를 입력해주세요.")
      return
    }

    if (newPassword === "") {
      alert("새 비밀번호를 입력해주세요.")
      return
    }

    if (newPassword !== confirmPassword) {
      alert("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
      return
    }

    // 여기서 실제 비밀번호 변경 API 호출
    alert("비밀번호가 변경되었습니다.")

    // 다이얼로그 닫기 및 상태 초기화
    setIsPasswordDialogOpen(false)
    setCurrentPassword("")
    setNewPassword("")
    setConfirmPassword("")
  }

  return (
      <div className="container mx-auto py-8 px-4">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">내 프로필</h1>
          <p className="text-slate-500">계정 정보 및 투자 내역을 관리하세요.</p>
        </div>

        <div className="grid gap-6 md:grid-cols-3">
          <Card className="md:col-span-1">
            <CardContent className="pt-6">
              <div className="flex flex-col items-center space-y-4">
                {/* 프로필 사진 업로드 기능 */}
                <div className="relative group">
                  <Avatar
                      className="h-24 w-24 cursor-pointer group-hover:opacity-80 transition-opacity"
                      onClick={handleAvatarClick}
                  >
                    <AvatarImage src={avatarSrc || "/placeholder.svg"} alt="프로필 이미지" />
                    <AvatarFallback>사용자</AvatarFallback>
                  </Avatar>
                  <div
                      className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                      onClick={handleAvatarClick}
                  >
                    <div className="bg-black bg-opacity-50 rounded-full p-2">
                      <Camera className="h-6 w-6 text-white" />
                    </div>
                  </div>
                  <input type="file" ref={fileInputRef} className="hidden" accept="image/*" onChange={handleFileChange} />
                </div>
                <div className="text-center">
                  <div className="flex items-center justify-center gap-2">
                    <h2 className="text-xl font-bold">{nickname}</h2>
                    <button
                        className="text-slate-500 hover:text-slate-700"
                        onClick={() => document.getElementById("nickname-input")?.focus()}
                    >
                      <Pencil className="h-4 w-4" />
                    </button>
                  </div>
                  <p className="text-sm text-slate-500">@{userId}</p> {/* 이메일 대신 아이디 표시 */}
                </div>
                <div className="w-full pt-4">
                  <div className="flex justify-between py-2 border-b">
                    <span className="text-slate-500">가입일</span>
                    <span>2023년 5월 15일</span>
                  </div>
                  <div className="flex justify-between py-2 border-b">
                    <span className="text-slate-500">투자 성향</span>
                    <span>성장형</span>
                  </div>
                  <div className="flex justify-between py-2 border-b">
                    <span className="text-slate-500">관심 테마</span>
                    <span>기술, 에너지</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardHeader>
              <Tabs defaultValue="account">
                <TabsList className="grid w-full grid-cols-3">
                  <TabsTrigger value="account">계정 정보</TabsTrigger>
                  <TabsTrigger value="portfolio">포트폴리오</TabsTrigger>
                  <TabsTrigger value="history">투자 내역</TabsTrigger>
                </TabsList>

                <TabsContent value="account" className="space-y-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="nickname-input">닉네임</Label>
                    <Input id="nickname-input" value={nickname} onChange={(e) => setNickname(e.target.value)} />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="user-id">아이디</Label> {/* 이메일 대신 아이디로 변경 */}
                    <Input id="user-id" value={userId} onChange={(e) => setUserId(e.target.value)} disabled />
                    <p className="text-xs text-slate-500">아이디는 변경할 수 없습니다.</p>
                  </div>

                  {/* ETF 내역 공개 여부 설정 */}
                  <div className="flex items-center justify-between space-x-2 pt-2">
                    <Label htmlFor="portfolio-public">ETF 내역 공개</Label>
                    <Switch id="portfolio-public" checked={isPublicPortfolio} onCheckedChange={setIsPublicPortfolio} />
                  </div>
                  <p className="text-xs text-slate-500">
                    {isPublicPortfolio
                        ? "다른 사용자가 내 ETF 내역을 볼 수 있습니다."
                        : "내 ETF 내역은 비공개로 설정되어 있습니다."}
                  </p>

                  <Button onClick={handleProfileUpdate}>정보 수정</Button>

                  {/* 비밀번호 변경 다이얼로그 */}
                  <div className="pt-4 border-t mt-4">
                    <h3 className="text-lg font-medium mb-2">비밀번호 변경</h3>
                    <Dialog open={isPasswordDialogOpen} onOpenChange={setIsPasswordDialogOpen}>
                      <DialogTrigger asChild>
                        <Button variant="outline">비밀번호 변경</Button>
                      </DialogTrigger>
                      <DialogContent className="sm:max-w-[425px]">
                        <DialogHeader>
                          <DialogTitle>비밀번호 변경</DialogTitle>
                          <DialogDescription>현재 비밀번호를 입력하고 새 비밀번호를 설정하세요.</DialogDescription>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                          <div className="grid gap-2">
                            <Label htmlFor="current-password">현재 비밀번호</Label>
                            <Input
                                id="current-password"
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                            />
                          </div>
                          <div className="grid gap-2">
                            <Label htmlFor="new-password">새 비밀번호</Label>
                            <Input
                                id="new-password"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                            />
                          </div>
                          <div className="grid gap-2">
                            <Label htmlFor="confirm-password">비밀번호 확인</Label>
                            <Input
                                id="confirm-password"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                            />
                          </div>
                        </div>
                        <DialogFooter>
                          <Button variant="outline" onClick={() => setIsPasswordDialogOpen(false)}>
                            취소
                          </Button>
                          <Button onClick={handlePasswordChange}>변경하기</Button>
                        </DialogFooter>
                      </DialogContent>
                    </Dialog>
                  </div>
                </TabsContent>

                <TabsContent value="portfolio" className="mt-4">
                  <div className="space-y-4">
                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">KODEX 삼성전자</h3>
                        <span className="text-green-600 font-bold">+12.5%</span>
                      </div>
                      <div className="flex justify-between text-sm mb-2">
                        <span>보유 수량: 10주</span>
                        <span>평균 매수가: 78,500원</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>현재가: 82,500원</span>
                        <span className="text-green-600">수익: +40,000원</span>
                      </div>
                    </div>

                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">TIGER 2차전지</h3>
                        <span className="text-green-600 font-bold">+8.2%</span>
                      </div>
                      <div className="flex justify-between text-sm mb-2">
                        <span>보유 수량: 5주</span>
                        <span>평균 매수가: 39,100원</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>현재가: 42,300원</span>
                        <span className="text-green-600">수익: +16,000원</span>
                      </div>
                    </div>

                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">TIGER 미국나스닥100</h3>
                        <span className="text-red-600 font-bold">-2.3%</span>
                      </div>
                      <div className="flex justify-between text-sm mb-2">
                        <span>보유 수량: 8주</span>
                        <span>평균 매수가: 22,000원</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>현재가: 21,500원</span>
                        <span className="text-red-600">수익: -4,000원</span>
                      </div>
                    </div>
                  </div>
                </TabsContent>

                <TabsContent value="history" className="mt-4">
                  <div className="space-y-4">
                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">KODEX 삼성전자 매수</h3>
                        <span className="text-sm text-slate-500">2023-10-15</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>수량: 5주</span>
                        <span>가격: 78,500원</span>
                      </div>
                    </div>

                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">TIGER 2차전지 매수</h3>
                        <span className="text-sm text-slate-500">2023-09-22</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>수량: 5주</span>
                        <span>가격: 39,100원</span>
                      </div>
                    </div>

                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">KODEX 삼성전자 매수</h3>
                        <span className="text-sm text-slate-500">2023-08-05</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>수량: 5주</span>
                        <span>가격: 78,500원</span>
                      </div>
                    </div>

                    <div className="border rounded-lg p-4">
                      <div className="flex justify-between items-center mb-2">
                        <h3 className="font-semibold">TIGER 미국나스닥100 매수</h3>
                        <span className="text-sm text-slate-500">2023-07-18</span>
                      </div>
                      <div className="flex justify-between text-sm">
                        <span>수량: 8주</span>
                        <span>가격: 22,000원</span>
                      </div>
                    </div>
                  </div>
                </TabsContent>
              </Tabs>
            </CardHeader>
          </Card>
        </div>
      </div>
  )
}
