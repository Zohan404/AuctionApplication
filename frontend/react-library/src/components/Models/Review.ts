import { ListedUserModel } from "./ListedUserModel";

export class Review {
    constructor(
        public user: ListedUserModel,
        public text: string,
        public rating: number) { }
}