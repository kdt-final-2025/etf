import { cookies } from 'next/headers';
import SidebarClient from '@/components/sidebar-client';

export default async function SidebarServer() {
  // 1) 서버에서 쿠키 읽기
  const cookieStore = await cookies();
  const loginId = cookieStore.get('login_id')?.value || '';
  const isLoggedIn = cookieStore.has('accessToken');
  // 2) 클라이언트 컴포넌트에 loginId 내려주기
  return <SidebarClient loginId={loginId} isLoggedIn={isLoggedIn} />;
}
