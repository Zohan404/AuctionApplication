import { AuctionBadge } from "./AuctionBadge";
import { ListedUserModel } from "./ListedUserModel";

export class Bid {
    constructor(
        public user: ListedUserModel,
        public auction: AuctionBadge,
        public amount: number,
        public time: Date
    ) { }
}