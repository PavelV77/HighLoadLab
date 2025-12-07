import { UUID } from "crypto";

export interface CommentDto {
    id: UUID,
    newsId: UUID,
    userId: UUID,
    body: string,
    insertAt: number,
    updateAt: number;
}
