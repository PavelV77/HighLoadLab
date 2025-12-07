import { UUID } from "crypto";

export interface News {
    id: UUID;
    head: string;
    body: string;
    userId: UUID;
    insertAt: number;
    updateAt: number;
    countLike: number;
    countDislike: number;
}
