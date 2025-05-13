"use client"

import { useState } from "react";
import { login } from "./actions";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function LoginPage() {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const router = useRouter();

  const handleLogin = async () => {
    if (!loginId || !password) {
      setErrorMessage("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    setIsLoading(true);
    setErrorMessage("");

    try {
      const result = await login(loginId, password);

      if (result.success) {
        // 클라이언트에서 처리
        alert(result.message);
        router.push("/"); // 홈으로 이동
        router.refresh(); // 페이지 새로고침으로 상태 갱신
      } else {
        setErrorMessage(result.message);
      }
    } catch (error) {
      setErrorMessage("로그인 처리 중 오류가 발생했습니다.");
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

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
                  onKeyDown={(e) => e.key === 'Enter' && handleLogin()}
              />
            </div>
            {errorMessage && (
                <div className="text-red-500 text-sm">{errorMessage}</div>
            )}
          </CardContent>
          <CardFooter className="flex flex-col space-y-4">
            <Button
                className="w-full"
                onClick={handleLogin}
                disabled={isLoading}
            >
              {isLoading ? "로그인 중..." : "로그인"}
            </Button>
            <div className="text-center text-sm">
              계정이 없으신가요?{" "}
              <Link href="/register" className="text-blue-600 hover:underline">
                회원가입
              </Link>
            </div>
          </CardFooter>
        </Card>
      </div>
  );
}