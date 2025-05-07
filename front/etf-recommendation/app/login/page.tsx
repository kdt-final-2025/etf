"use client"
import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {cookies} from "next/headers";

export default function LoginPage() {
  const [loginId, setLoginId] = useState("")
  const [password, setPassword] = useState("")
  const router = useRouter()

  const handleLogin = async () => {
    try {

      const res = await fetch("http://localhost:8080/api/v1/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          loginId: loginId,
          password: password
        }),
      })

      if (res.ok) {
        const data = await res.json()
        alert("로그인 성공!")
        document.cookie = `accessToken=${data.token}; path=/; secure; samesite=strict`;
        router.push("/") // 로그인 성공 후 메인으로 이동
      } else {
        const error = await res.text()
        alert("로그인 실패: " + error)
      }
    } catch (err) {
      console.error(err)
      alert("서버 오류")
    }
  }

  return (
      <div className="container mx-auto flex items-center justify-center min-h-screen py-8 px-4">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-2xl">로그인</CardTitle>
            <CardDescription>계정에 로그인하여 맞춤형 ETF 추천을 받아보세요.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="loginId">아이디</Label>
              <Input
                  id="loginId"
                  type="text"
                  placeholder="아이디를 입력하세요"
                  value={loginId}
                  onChange={(e) => setLoginId(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                  id="password"
                  type="password"
                  placeholder="비밀번호를 입력하세요"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </CardContent>
          <CardFooter className="flex flex-col space-y-4">
            <Button className="w-full" onClick={handleLogin}>로그인</Button>
            <div className="text-center text-sm">
              계정이 없으신가요?{" "}
              <Link href="/register" className="text-blue-600 hover:underline">
                회원가입
              </Link>
            </div>
          </CardFooter>
        </Card>
      </div>
  )
}
