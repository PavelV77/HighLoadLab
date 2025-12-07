import { UUID } from "crypto";

export interface Like {
    id: UUID;
    typeOfActivity: number;
    userId: UUID;
    newsId: UUID;
    insertAt: number;
    updateAt: number;
}
