import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { AuctionCard } from "../../AuctionPage/AuctionCard";
import { AuctionCardPlaceHolder } from "../../AuctionPage/AuctionCardPlaceHolder";
import { ListedAuctionModel } from "../../Models/ListedAuctionModel";
import { User } from "../../Models/User";
import { NoComponent } from "../../Utils/NoComponent";
import { Spinner } from "../../Utils/Spinner";
import { useAuthService } from "../../Utils/useAuthService";

export const UserAuctionsSubPage: React.FC = () => {
    const { userId } = useParams();

    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const [loading, setLoading] = useState<boolean>(true);
    const [auctions, setAuctions] = useState<ListedAuctionModel[]>([]);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);

    const fetchAuctions = () => {
        const token = localStorage.getItem("token");

        setLoading(true);
        const params = new URLSearchParams();
        params.set("page", page.toString());

        fetch(`http://localhost:8081/api/auctions/byUser/${userId}?${params.toString()}`, {
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
                setAuctions(json);
            })
            .catch()
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        fetchAuctions();
    }, [userId, page]);

    useEffect(() => {
        const handleScroll = () => {
            if (window.innerHeight + window.scrollY >= document.body.offsetHeight && hasMore && !loading) {
                setPage(prev => prev + 1);
            }
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, [hasMore, loading]);

    return (
        <>
            <div className="my-3 px-3">
                {(loading && page === 0)
                    ?
                    <div className="row row-cols-md-3 row-cols-sm-2 row-cols-1 g-4">
                        <AuctionCardPlaceHolder />
                        <AuctionCardPlaceHolder />
                        <AuctionCardPlaceHolder />
                    </div>
                    :
                    userId === loggedInUser?.id + "" || auctions.length !== 0
                        ?
                        <div className="row row-cols-md-3 row-cols-sm-2 row-cols-1 g-4">
                            {
                                userId === loggedInUser?.id + "" && <AuctionCard />
                            }
                            {
                                auctions.map((auction) => <AuctionCard key={auction.id} auction={auction} />)
                            }
                        </div>
                        :
                        <NoComponent type={"auctions"} size={200} />
                }
            </div>
            {(loading && page !== 0) &&
                <Spinner />
            }
        </>
    );
}