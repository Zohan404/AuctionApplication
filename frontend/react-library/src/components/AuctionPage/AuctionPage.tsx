import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { ListedAuctionModel } from "../Models/ListedAuctionModel";
import { NoComponent } from "../Utils/NoComponent";
import { Spinner } from "../Utils/Spinner";
import { useAuthService } from "../Utils/useAuthService";
import { AuctionCard } from "./AuctionCard";
import { AuctionCardPlaceHolder } from "./AuctionCardPlaceHolder";

export const AuctionPage = () => {
    const { handleLogOut } = useAuthService();
    const [searchParams, setSearchParams] = useSearchParams(new URLSearchParams(
        [["statuses", "active"], ["statuses", "upcoming"], ["types", "secret"], ["types", "public"]]));
    const [title, setTitle] = useSearchParams();
    const [triggerSearch, setTriggerSearch] = useState<number>(0);

    const [loading, setLoading] = useState<boolean>(true);
    const [auctions, setAuctions] = useState<ListedAuctionModel[]>([]);
    const [page, setPage] = useState<number>(0);
    const [hasMore, setHasMore] = useState<boolean>(true);

    const [isCheckedMaxNextPrice, setCheckedMaxNextPrice] = useState<boolean>(false);
    const [isCheckedMaxBuyNowPrice, setCheckedMaxBuyNowPrice] = useState<boolean>(false);
    const [maxNextPrice, setMaxNextPrice] = useState<string>("50000");
    const [maxBuyNowPrice, setMaxBuyNowPrice] = useState<string>("50000");

    const [isCheckedClosed, setCheckedClosed] = useState<boolean>(false);
    const [isCheckedAvailable, setCheckedAvailable] = useState<boolean>(true);
    const [isCheckedUpcoming, setCheckedUpcoming] = useState<boolean>(true);

    const [isCheckedNormalAuctions, setCheckedNormalAuctions] = useState<boolean>(true);
    const [isCheckedSecretAuctions, setCheckedSecretAuctions] = useState<boolean>(true);

    const fetchAuctions = () => {
        const token = localStorage.getItem("token");

        setLoading(true);
        const params = new URLSearchParams(searchParams);
        params.set("page", page.toString());

        fetch(`http://localhost:8081/api/auctions?${params.toString()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                console.log(res.status)
                if (res.status === 401 || res.status === 403) {
                    handleLogOut();
                    return Promise.reject(new Error('Authentication failed'));
                }
                return res.json()
            })
            .then((json) => {
                if (json.length < 10) setHasMore(false);
                setAuctions(prev => [...prev, ...json]);
            })
            .catch()
            .finally(() => setLoading(false));

        setCheckedMaxNextPrice(searchParams.get("maxNextPrice") !== null);
        searchParams.get("maxNextPrice") !== null && setMaxNextPrice(searchParams.get("maxNextPrice") || "50000");
        setCheckedMaxBuyNowPrice(searchParams.get("maxBuyNowPrice") !== null);
        searchParams.get("maxBuyNowPrice") !== null && setMaxBuyNowPrice(searchParams.get("maxBuyNowPrice") || "50000");
        setCheckedClosed(searchParams.getAll("statuses").includes("closed"));
        setCheckedAvailable(searchParams.getAll("statuses").includes("active"));
        setCheckedUpcoming(searchParams.getAll("statuses").includes("upcoming"));
        setCheckedNormalAuctions(searchParams.getAll("types").includes("public"));
        setCheckedSecretAuctions(searchParams.getAll("types").includes("secret"));
    }

    useEffect(() => {
        fetchAuctions();
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

    const handleSearch = (subject: string) => {
        if (subject === "searchParams") setSearchParams(searchParams);
        if (subject === "title") setTitle(title);
        setTriggerSearch(prev => prev + 1);

        setPage(0);
        setAuctions([]);
        setHasMore(true);
    };

    const changeFilterParams = (type: string, value: any) => {
        switch (type) {
            case "maxNextPrice":
                if (value !== null) {
                    setCheckedMaxNextPrice(true);
                    setMaxNextPrice(value);
                    searchParams.set(type, value);
                } else if (!isCheckedMaxNextPrice) {
                    setCheckedMaxNextPrice(true);
                    searchParams.set(type, maxNextPrice);
                } else {
                    setCheckedMaxNextPrice(false);
                    searchParams.delete(type);
                }
                break;
            case "maxBuyNowPrice":
                if (value !== null) {
                    setCheckedMaxBuyNowPrice(true);
                    setMaxBuyNowPrice(value);
                    searchParams.set(type, value);
                } else if (!isCheckedMaxBuyNowPrice) {
                    setCheckedMaxBuyNowPrice(true);
                    searchParams.set(type, maxBuyNowPrice);
                } else {
                    setCheckedMaxBuyNowPrice(false);
                    searchParams.delete(type);
                }
                break;
            case "public":
                setCheckedNormalAuctions(value);
                if (!value) {
                    setCheckedSecretAuctions(true);
                    searchParams.delete("types");
                    searchParams.append("types", "secret");
                } else {
                    searchParams.append("types", "public");
                }
                break;
            case "secret":
                setCheckedSecretAuctions(value);
                if (!value) {
                    setCheckedNormalAuctions(true);
                    searchParams.delete("types");
                    searchParams.append("types", "public");
                } else {
                    searchParams.append("types", "secret");
                }
                break;
            default:
                switch (type) {
                    case "closed":
                        setCheckedClosed(value);
                        break;
                    case "active":
                        setCheckedAvailable(value);
                        break;
                    case "upcoming":
                        setCheckedUpcoming(value);
                        break;
                    default:
                        break;
                }
                if (value === true) {
                    searchParams.append("statuses", type);
                } else {
                    const statuses: string[] = searchParams.getAll("statuses").filter(status => status !== type);
                    searchParams.delete("statuses");
                    statuses.forEach(status => searchParams.append("statuses", status));
                }
                break;
        }
    }

    return (
        <main className="container">
            <h1 className="text-dark text-center fw-bold mt-3 mb-5">Auctions</h1>
            <div className="hstack gap-3">
                <input className="form-control me-auto" type="text" placeholder="Search for items..." aria-label="Search for item" onChange={(e) => title.set("title", e.target.value)} />
                <button type="button" className="btn btn-success" onClick={() => { handleSearch("title"); }}>Submit</button>
                <div className="vr"></div>
                <div className="btn-group">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => { handleSearch("searchParams"); }}>Filter</button>
                    <button type="button" className="btn btn-outline-secondary dropdown-toggle dropdown-toggle-split" data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
                        <span className="visually-hidden">Toggle Dropdown</span>
                    </button>
                    <ul className="dropdown-menu p-3" style={{ width: 350 }} >
                        <li className="form-check form-switch">
                            <label htmlFor="nextPriceRange" className="form-label">Maximum Next Price ({maxNextPrice}$)</label>
                            <input className="form-check-input" type="checkbox" role="switch" id="nextPriceRange" checked={isCheckedMaxNextPrice} onChange={(e) => changeFilterParams("maxNextPrice", null)}></input>
                            <input type="range" className="form-range" min="10" max="100000" value={maxNextPrice} step="10" id="nextPriceRange" disabled={!isCheckedMaxNextPrice} onChange={(e) => changeFilterParams("maxNextPrice", e.target.value)} />
                        </li>
                        <li><hr className="dropdown-divider" /></li>
                        <li className="form-check form-switch">
                            <label htmlFor="buyNowPriceRange" className="form-label">Maximum Buy Now Price ({maxBuyNowPrice}$)</label>
                            <input className="form-check-input" type="checkbox" role="switch" id="buyNowPriceRange" checked={isCheckedMaxBuyNowPrice} onChange={(e) => changeFilterParams("maxBuyNowPrice", null)}></input>
                            <input type="range" className="form-range" min="10" max="100000" value={maxBuyNowPrice} step="10" id="buyNowPriceRange" disabled={!isCheckedMaxBuyNowPrice} onChange={(e) => { changeFilterParams("maxBuyNowPrice", e.target.value) }} />
                        </li>
                        <li><hr className="dropdown-divider" /></li>
                        <li className="hstack justify-content-around">
                            <input type="checkbox" className="btn-check" name="options-outlined" id="closedAuctionsCheckBox" autoComplete="off" checked={isCheckedClosed} onChange={(e) => { changeFilterParams("closed", e.target.checked) }} />
                            <label className="btn btn-outline-danger" htmlFor="closedAuctionsCheckBox">Closed</label>

                            <input type="checkbox" className="btn-check" name="options-outlined" id="availableAuctionsCheckBox" autoComplete="off" checked={isCheckedAvailable} onChange={(e) => { changeFilterParams("active", e.target.checked) }} />
                            <label className="btn btn-outline-success" htmlFor="availableAuctionsCheckBox">Active</label>

                            <input type="checkbox" className="btn-check" name="options-outlined" id="upcomingAuctionsCheckBox" autoComplete="off" checked={isCheckedUpcoming} onChange={(e) => { changeFilterParams("upcoming", e.target.checked) }} />
                            <label className="btn btn-outline-secondary" htmlFor="upcomingAuctionsCheckBox">Upcoming</label>
                        </li>
                        <li><hr className="dropdown-divider" /></li>
                        <li>
                            <div className="form-check form-switch">
                                <input className="form-check-input" type="checkbox" role="switch" id="normalAuctionsCheck" checked={isCheckedNormalAuctions} onChange={(e) => { changeFilterParams("public", e.target.checked) }} />
                                <label className="form-check-label" htmlFor="normalAuctionsCheck">Public auctions</label>
                            </div>
                        </li>
                        <li>
                            <div className="form-check form-switch">
                                <input className="form-check-input" type="checkbox" role="switch" id="secretAuctionsCheck" checked={isCheckedSecretAuctions} onChange={(e) => { changeFilterParams("secret", e.target.checked) }} />
                                <label className="form-check-label" htmlFor="secretAuctionsCheck">Secret auctions</label>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
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
                                auctions.map((auction: ListedAuctionModel) => <AuctionCard key={auction.id} auction={auction} />)
                            }
                        </div>
                        :
                        <NoComponent type="auction" size={200} />
                }
            </div>
            {(loading && page !== 0) &&
                <Spinner />
            }
        </main>
    );
}