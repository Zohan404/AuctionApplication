import { useEffect, useState } from "react";
import { AuctionCard } from "../AuctionPage/AuctionCard";
import { AuctionCardPlaceHolder } from "../AuctionPage/AuctionCardPlaceHolder";
import { ListedAuctionModel } from "../Models/ListedAuctionModel";
import { User } from "../Models/User";
import { nameShower } from "../Utils/helper";
import { NoComponent } from "../Utils/NoComponent";
import { Spinner } from "../Utils/Spinner";
import { useAuthService } from "../Utils/useAuthService";
import { HomeCarousel } from "./HomeCarousel";

export const HomePage = () => {
    const { getLoggedInUser } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());

    const [loading, setLoading] = useState<boolean>(true);
    const [auctions, setAuctions] = useState<ListedAuctionModel[]>([]);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);

    const fetchAuctions = () => {
        const token = localStorage.getItem("token");

        setLoading(true);

        fetch(`http://localhost:8081/api/auctions/byFollowings?page=${page.toString()}`, {
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
                if (json.length < 12) setHasMore(false);
                setAuctions(json);
            })
            .catch()
            .finally(() => setLoading(false));
    }

    useEffect(() => {
        fetchAuctions();
    }, [page]);

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
        <main>
            <div className="text-center">
                <h1 className="text-dark fw-bold mt-3 mb-5">Home Page</h1>
                <p className="container px-5 secondary-text-color lead">Welcome <span className="fst-italic text-decoration-underline">{nameShower(loggedInUser)}</span> to JackPot, the ultimate place to bid and collect amazing deals. Wether you are searching for rare collectibles, every-day items, you can find here eveything from low to high prices. Check out our latest auctions or create a new one to roup your prduct! Start exploring today and get ready to <span className="fw-semibold">hit the JackPot</span>!</p>
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" className="bi bi-coin text-warning h1" viewBox="0 0 16 16">
                    <path d="M5.5 9.511c.076.954.83 1.697 2.182 1.785V12h.6v-.709c1.4-.098 2.218-.846 2.218-1.932 0-.987-.626-1.496-1.745-1.76l-.473-.112V5.57c.6.068.982.396 1.074.85h1.052c-.076-.919-.864-1.638-2.126-1.716V4h-.6v.719c-1.195.117-2.01.836-2.01 1.853 0 .9.606 1.472 1.613 1.707l.397.098v2.034c-.615-.093-1.022-.43-1.114-.9zm2.177-2.166c-.59-.137-.91-.416-.91-.836 0-.47.345-.822.915-.925v1.76h-.005zm.692 1.193c.717.166 1.048.435 1.048.91 0 .542-.412.914-1.135.982V8.518z" />
                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16" />
                    <path d="M8 13.5a5.5 5.5 0 1 1 0-11 5.5 5.5 0 0 1 0 11m0 .5A6 6 0 1 0 8 2a6 6 0 0 0 0 12" />
                </svg>
            </div>
            <HomeCarousel />
            <div className="container">
                <div className="display-3">For You</div>
                <div className="my-3 px-3">
                    {(loading && page === 0)
                        ?
                        <div className="row row-cols-md-3 row-cols-sm-2 row-cols-1 g-4">
                            <AuctionCardPlaceHolder />
                            <AuctionCardPlaceHolder />
                            <AuctionCardPlaceHolder />
                        </div>
                        :
                        auctions.length !== 0
                            ?
                            <div className="row row-cols-md-3 row-cols-sm-2 row-cols-1 g-4">
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
            </div>
        </main>
    );
}

function handleLogOut() {
    throw new Error("Function not implemented.");
}
