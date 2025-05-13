import { fetchCommentsPage } from "./comment/actions";
import CommentForm from "./comment/commentForm";
import CommentsList from "./comment/commentsList";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";

interface EtfDetailResponse {
  etfId: number;
  etfName: string;
  etfCode: string;
  companyName: string;
  listingDate: string;
}

export default async function EtfDetailPage({
  params,
}: {
  params: Promise<{ ticker: string }>;
}) {
  const { ticker } = await params;
  const etfId = Number(ticker);

  const detailRes = await fetch(`http://localhost:8080/api/v1/etfs/${etfId}`);
  if (!detailRes.ok) {
    return (
      <div className="p-8 text-center text-red-600">
        ETF 정보를 불러오지 못했습니다.
      </div>
    );
  }
  const data: EtfDetailResponse = await detailRes.json();
  const formattedDate = new Date(data.listingDate).toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });

  // 3) 페이징된 댓글 목록 fetch
  const commentsPage = await fetchCommentsPage(etfId, 0, 20);

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      <h1 className="text-3xl font-bold">{data.etfName}</h1>

      <Tabs defaultValue="details" className="bg-white shadow-lg rounded-lg">
        <TabsList className="grid grid-cols-2">
          <TabsTrigger
            value="details"
            className="py-2 text-center font-medium hover:bg-gray-100 transition"
          >
            상세 정보
          </TabsTrigger>
          <TabsTrigger
            value="comments"
            className="py-2 text-center font-medium hover:bg-gray-100 transition"
          >
            댓글 ({commentsPage.totalElements})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="details" className="p-6 space-y-4">
          <div className="grid grid-cols-2 gap-6 text-gray-700">
            <div>
              <p className="font-semibold">ETF 코드</p>
              <p>{data.etfCode}</p>
            </div>
            <div>
              <p className="font-semibold">운용사</p>
              <p>{data.companyName}</p>
            </div>
            <div>
              <p className="font-semibold">상장일</p>
              <p>{formattedDate}</p>
            </div>
          </div>
        </TabsContent>

        <TabsContent value="comments" className="p-6 space-y-6">
          <CommentsList comments={commentsPage.commentResponses} />
          <CommentForm etfId={etfId} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
