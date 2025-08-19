import {User} from "../Models/User";

export const useAuthService = () => {
    
    const handleLogOut = (): void => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
    }

    const getLoggedInUser = (): User | null => {
        const userString: string | null = localStorage.getItem("user");
        return userString ? JSON.parse(userString) : null;
    }

    const changeLoggedInUser = (changedUser: User) => {
        localStorage.removeItem("user");
        localStorage.setItem("user", JSON.stringify(changedUser));
        window.dispatchEvent(new Event('userUpdated'));
    }

    return { getLoggedInUser, changeLoggedInUser, handleLogOut};
}