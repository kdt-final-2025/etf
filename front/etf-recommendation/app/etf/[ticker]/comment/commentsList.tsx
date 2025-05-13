import { CommentResponse } from "./actions";

export default function CommentsList({
  comments,
}: {
  comments: CommentResponse[];
}) {
  if (comments.length === 0) {
    return <p className="text-center text-gray-500">등록된 댓글이 없습니다.</p>;
  }

  return (
    <ul className="space-y-4">
      {comments.map((c) => (
        <li
          key={c.id}
          className="p-4 border rounded-lg hover:shadow transition-shadow"
        >
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center space-x-2">
              {c.imageUrl && (
                <img
                  src={c.imageUrl}
                  alt={c.nickName}
                  className="w-12 h-12 rounded-full object-cover"
                />
              )}
              <span className="font-medium">{c.nickName}</span>
            </div>
            <span className="text-xs text-gray-400">
              {new Date(c.createdAt).toLocaleString("ko-KR")}
            </span>
          </div>
          <p className="whitespace-pre-wrap">{c.content}</p>
          <div className="mt-2 text-sm text-gray-500">
            좋아요 {c.likesCount}
          </div>
        </li>
      ))}
    </ul>
  );
}
