import { Bid } from "./Bid";
import { ListedUserModel } from "./ListedUserModel";

export class InspectedAuctionModel {
    constructor(
        public id: number,
        public title: string,
        public description: string,
        public nextPrice: number,
        public startDate: Date,
        public endDate: Date,
        public isSecret: boolean,
        public createdBy: ListedUserModel,
        public pictures: string[],
        public bids: Bid[],
        public bidIncrement?: number,
        public buyNowPrice?: number,
        public winner?: ListedUserModel
    ) { }
}