import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Bid } from "../../Models/Bid";
import { BidListPlaceHolder } from "../../Utils/BidListPlaceHolder";
import { NoComponent } from "../../Utils/NoComponent";
import { Spinner } from "../../Utils/Spinner";
import { useAuthService } from "../../Utils/useAuthService";

export const UserBidsSubPage: React.FC = (props) => {
    const { userId } = useParams();

    const { handleLogOut } = useAuthService();

    const [loading, setLoading] = useState<boolean>(true);
    const [bids, setBids] = useState<Bid[]>([]);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);


    const fetchBids = () => {
        const token = localStorage.getItem("token");

        setLoading(true);
        const params = new URLSearchParams();
        params.set("page", page.toString());

        fetch(`http://localhost:8081/api/auctions/bids/byUser/${userId}?${params.toString()}`, {
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
                setBids(json);
            })
            .catch()
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        fetchBids();
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
            {(loading && page === 0)
                ?
                <ul className="list-group rounded-top-0 placeholder-glow">
                    {[...Array(2)].map((_, index) => (
                        <BidListPlaceHolder key={index} displaySize={6} imageSize={100} isRounded={false} />
                    ))}
                </ul>
                :
                bids.length === 0
                    ?
                    <NoComponent type="bids" size={100} />
                    :
                    <ul className="list-group rounded-top-0">
                        {bids.map((bid, index) =>
                            <li key={index} className={`list-group-item d-flex align-items-center justify-content-between px-3
                        ${bid.auction.winnerId + "" === userId && "bg-warning"}`} >
                                <Link className="d-flex align-items-center" to={`../../auctions/${bid.auction.id}`}>
                                    <img src={bid.auction.frontPicture} width={100} alt={bid.auction.title} />
                                    <h2 className="display-6 fw-bold ms-2">{bid.auction.title}</h2>
                                </Link>
                                {bid.amount && <div className="h1"><span className="badge bg-primary rounded-pill">{bid.amount}$</span></div>}
                            </li>)}
                    </ul>
            }
            {(loading && page !== 0) &&
                <Spinner />
            }
        </>
    );
}