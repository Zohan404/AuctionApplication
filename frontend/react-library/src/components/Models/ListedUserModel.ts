export class ListedUserModel{
    constructor(
        public id: number,
        public firstName: string,
        public lastName: string,
        public location: string,
        public profilePicture: string,
        public followers: number[],
        public followings: number[],
        public onGoingAuctions: number[],
        public soldItems: number[],
        public middleName?: string,
    ) { }
}