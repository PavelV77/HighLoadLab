import { UUID } from "crypto";

export interface User {
    id: UUID;
    login: string;
    insertAt: number;
    updateAt: number;
}
