import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"

export default function ProfilePage() {
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
              <Avatar className="h-24 w-24">
                <AvatarImage src="/placeholder.svg?height=96&width=96" alt="프로필 이미지" />
                <AvatarFallback>사용자</AvatarFallback>
              </Avatar>
              <div className="text-center">
                <h2 className="text-xl font-bold">홍길동</h2>
                <p className="text-sm text-slate-500">hong@example.com</p>
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
                  <Label htmlFor="name">이름</Label>
                  <Input id="name" defaultValue="홍길동" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email">이메일</Label>
                  <Input id="email" type="email" defaultValue="hong@example.com" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="password">새 비밀번호</Label>
                  <Input id="password" type="password" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="confirmPassword">비밀번호 확인</Label>
                  <Input id="confirmPassword" type="password" />
                </div>
                <Button>정보 수정</Button>
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
