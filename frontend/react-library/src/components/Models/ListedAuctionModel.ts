import { ListedUserModel } from "./ListedUserModel";

export class ListedAuctionModel {
    constructor(
        public id: number,
        public title: string,
        public description: string,
        public nextPrice: number,
        public startDate: Date,
        public endDate: Date,
        public createdBy: ListedUserModel,
        public frontPicture: string,
        public createdTime: Date,
        public isSecret: boolean,
        public winner?: ListedUserModel
    ) {}
}