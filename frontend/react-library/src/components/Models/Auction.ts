import { ListedUserModel } from "./ListedUserModel";

export class Auction{
    constructor(
        public id: number,
        public title: string,
        public description: string,
        public startingPrice: number,
        public pictures: string[],
        public isSecret: boolean,
        public startDate?: Date,
        public endDate?: Date,
        public createdBy?: ListedUserModel,
        public bidIncrement?: number,
        public buyNowPrice?: number,
        public winner?: ListedUserModel
    ) { }
}