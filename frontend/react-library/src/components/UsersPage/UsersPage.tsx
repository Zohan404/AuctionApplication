import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { ListedUserModel } from "../Models/ListedUserModel";
import { CountryDropdown } from "../Utils/CountryDropdown";
import { NoComponent } from "../Utils/NoComponent";
import { Spinner } from "../Utils/Spinner";
import { useAuthService } from "../Utils/useAuthService";
import { UserList } from "./UserList";
import { UserListPlaceHolder } from "./UserListPlaceHolder";

export const UsersPage = () => {
    const { handleLogOut } = useAuthService();

    const [searchParams, setSearchParams] = useSearchParams();
    const [title, setTitle] = useState<string>("");
    const [triggerSearch, setTriggerSearch] = useState<number>(0);

    const [loading, setLoading] = useState<boolean>(true);
    const [users, setUsers] = useState<ListedUserModel[]>([]);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);

    const fetchUsers = () => {
        const token = localStorage.getItem("token");

        setLoading(true);
        const params = new URLSearchParams(searchParams);
        params.set("page", page.toString());

        fetch(`http://localhost:8081/api/users?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (res.status === 401 || res.status === 403) {
                    handleLogOut();
                    return Promise.reject(new Error('Authentication failed'));
                }
                return res.json();
            })
            .then((json) => {
                if (json.length < 10) setHasMore(false);
                setUsers(prev => [...prev, ...json]);
                
            })
            .catch()
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        fetchUsers();
    }, [triggerSearch, page]);

    useEffect(() => {
        const handleScroll = () => {
            if (window.innerHeight + window.scrollY >= document.body.offsetHeight && hasMore && !loading) {
                setPage(prev => prev + 1);
            }
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, [hasMore, loading]);

    const handleSearch = () => {
        setSearchParams(searchParams);
        setTriggerSearch(prev => prev + 1);
        setPage(0);
        setUsers([]);
        setHasMore(true);
    };

    const onSelected = (code: string) => {
        if (code !== "NO_COUNTRY") {
            searchParams.set("country", code);
        } else {
            searchParams.delete("country");
        }
        handleSearch();
    }

    return (
        <main className="container">
            <h1 className="text-dark text-center fw-bold mt-3 mb-5">Users</h1>
            <div className="hstack gap-3">
                <input className="form-control me-auto" type="text" placeholder="Search for users..."
                    aria-label="Search for item" onChange={(e) => setTitle(e.target.value)} />
                <button type="button" className="btn btn-success" onClick={() => { searchParams.set("title", title); handleSearch(); }}>
                    Submit
                </button>
                <div className="vr" />
                <CountryDropdown noCountryText="Filter countries" buttonColor="btn-outline-secondary"
                    onSelected={onSelected} />
            </div>
            <ul className="list-group my-5 gap-3">
                {(loading && page === 0)
                    ?
                    <>
                        <UserListPlaceHolder shadow={true} />
                        <UserListPlaceHolder shadow={true} />
                        <UserListPlaceHolder shadow={true} />
                    </>
                    :
                    users.length === 0
                        ?
                        <NoComponent type="users" size={200} />
                        :
                        users.map(user => <UserList user={user} shadow={true} key={user.id} />)
                }
            </ul>
            {(loading && page !== 0) &&
                <Spinner />
            }
        </main >
    );
}