import { ListedUserModel } from "../Models/ListedUserModel";
import { User } from "../Models/User";

export const nameShower = (user: ListedUserModel | User | null) => {
    if (user === null) return "";
    return user.firstName + " " + user.middleName + " " + user.lastName;
}

export const dateToString = (date: Date) => {
    return date.toString().split('T')[0].split('-').reverse().join('.');
}

export const getTransformedImageUrl = (originalUrl: string, width: number, height: number, face: boolean = false) => {
    return originalUrl.replace("/upload/", `/upload/c_fill,w_${width},h_${height}${face ? ",g_face" : ""}/`);
}