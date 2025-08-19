export class User{
    constructor(
        public firstName: string,
        public lastName: string,
        public password: string,
        public email: string,
        public location: string,
        public dateOfBirth?: Date,
        public middleName?: string,
        public profilePicture?: string,
        public id?: number
    ) { }
}