import { Card, CardContent } from "@/components/ui/card";
import { fetchEconomicArticles } from "@/lib/api/article";
import Image from "next/image";

export default async function NewsPage() {
  const { data: articles } = await fetchEconomicArticles();

  return (
    <div className="container mx-auto py-8 px-4">
      <h1 className="text-3xl font-bold mb-6">경제 뉴스</h1>
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {!articles && <div>일시적으로 기사를 표시할 수 없습니다</div>}
        {articles &&
          articles.map((item) => (
            <Card
              key={item.id}
              className="h-full hover:shadow-md transition-shadow cursor-pointer overflow-hidden"
            >
              <a
                href={item.sourceUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="block h-full"
              >
                <div className="relative w-full h-40">
                  <Image
                    src={item.thumbnailUrl || "/placeholder.svg"}
                    alt={item.title || "뉴스 이미지"}
                    fill
                    className="object-cover"
                  />
                </div>
                <CardContent className="p-3">
                  <h3 className="font-medium line-clamp-2 text-sm">
                    {item.title}
                  </h3>
                </CardContent>
              </a>
            </Card>
          ))}
      </div>
    </div>
  );
}
