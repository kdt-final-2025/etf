import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { fetchEconomicNews } from "@/lib/news-service"
import Image from "next/image"

export default async function NewsPage() {
  const news = await fetchEconomicNews()

  // 뉴스 카테고리 분류 및 각 카테고리별 최대 10개만 표시
  const allNews = news.slice(0, 10)
  const marketNews = news.filter((item) => item.category === "시장동향").slice(0, 10)
  const etfNews = news.filter((item) => item.category === "ETF").slice(0, 10)
  const economyNews = news.filter((item) => item.category === "경제일반").slice(0, 10)
  const globalNews = news.filter((item) => item.category === "글로벌").slice(0, 10)

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">경제 뉴스</h1>
        <p className="text-slate-500">최신 경제 및 ETF 관련 뉴스를 확인하세요.</p>
      </div>

      <Tabs defaultValue="all">
        <div className="flex justify-between items-center mb-4">
          <TabsList>
            <TabsTrigger value="all">전체</TabsTrigger>
            <TabsTrigger value="market">시장동향</TabsTrigger>
            <TabsTrigger value="etf">ETF</TabsTrigger>
            <TabsTrigger value="economy">경제일반</TabsTrigger>
            <TabsTrigger value="global">글로벌</TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="all">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            {allNews.map((item) => (
              <NewsCard key={item.id} news={item} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="market">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            {marketNews.map((item) => (
              <NewsCard key={item.id} news={item} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="etf">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            {etfNews.map((item) => (
              <NewsCard key={item.id} news={item} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="economy">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            {economyNews.map((item) => (
              <NewsCard key={item.id} news={item} />
            ))}
          </div>
        </TabsContent>

        <TabsContent value="global">
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
            {globalNews.map((item) => (
              <NewsCard key={item.id} news={item} />
            ))}
          </div>
        </TabsContent>
      </Tabs>
    </div>
  )
}

function NewsCard({ news }: { news: any }) {
  return (
    <a href={news.url} target="_blank" rel="noopener noreferrer">
      <Card className="h-full hover:shadow-md transition-shadow cursor-pointer overflow-hidden">
        <div className="relative w-full h-40">
          <Image
            src={news.imageUrl || `/placeholder.svg?height=160&width=320`}
            alt={news.title}
            fill
            className="object-cover"
          />
        </div>
        <CardContent className="p-3">
          <h3 className="font-medium line-clamp-2 text-sm">{news.title}</h3>
        </CardContent>
      </Card>
    </a>
  )
}
