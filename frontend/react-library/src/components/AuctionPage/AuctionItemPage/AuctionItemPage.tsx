import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { InspectedAuctionModel } from "../../Models/InspectedAuctionModel";
import { User } from "../../Models/User";
import { BidListPlaceHolder } from "../../Utils/BidListPlaceHolder";
import { dateToString, nameShower } from "../../Utils/helper";
import { ModalHeader } from "../../Utils/ModalHeader";
import { NoComponent } from "../../Utils/NoComponent";
import { ProfilePicture } from "../../Utils/ProfilePicture";
import { useAuthService } from "../../Utils/useAuthService";
import { useFormErrors } from "../../Utils/useFormErrors";

export const AuctionItemPage = () => {
    const navigate = useNavigate();
    const { getLoggedInUser, handleLogOut } = useAuthService();
    const [loggedInUser] = useState<User | null>(getLoggedInUser());
    const { auctionId } = useParams();
    const { errors, handleBackendErrors, setFieldError, clearErrors } = useFormErrors();
    const [success, setSuccess] = useState<number>(0);

    const [bidPrice, setBidPrice] = useState<string>("");
    const [auction, setAuction] = useState<InspectedAuctionModel>();
    const [slide, setSlide] = useState<number>(0);

    useEffect(() => {
        const token = localStorage.getItem("token");

        fetch(`http://localhost:8081/api/auctions/${auctionId}`, {
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
                return res.json()
            })
            .then((json) => {
                setAuction(json);
                if (!json.isSecret && json.buyNowPrice && json.bidIncrement) {
                    setBidPrice(() => {
                        const middle = (json.buyNowPrice - json.nextPrice) / 2;
                        const middleDivisibleByIncrement = Math.floor(middle / json.bidIncrement) * json.bidIncrement;
                        return "" + (json.nextPrice + middleDivisibleByIncrement);
                    });
                }
                if (json.isSecret == true) {
                    setBidPrice(json.nextPrice);
                }
            })
            .catch(() => navigate("../not_found"));
    }, [auctionId, errors, success]);

    useEffect(() => {
        if (auction?.isSecret) document.body.className = "secondary-color";

        return () => {
            document.body.className = "";
        };
    }, [auction]);

    const handleBid = async () => {
        try {
            clearErrors();
            const token = localStorage.getItem("token");

            const response = await fetch(`http://localhost:8081/api/auctions/${auctionId}/bid`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: bidPrice,
            });

            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }
            if (response.status === 403 || response.status === 401) {
                handleLogOut();
                return;
            }

            setSuccess(success + 1);
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    }

    const handleBuyNow = async () => {
        try {
            const token = localStorage.getItem("token");

            const response = await fetch(`http://localhost:8081/api/auctions/${auctionId}/buy`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                }
            });

            if (!response.ok) {
                await handleBackendErrors(response);
                return;
            }
            if (response.status === 403 || response.status === 401) {
                handleLogOut();
                return;
            }

            setSuccess(success + 1);
        } catch (err: any) {
            setFieldError("general", err.message);
        }
    }

    return (
        auction === null || auction === undefined
            ?
            <main className="container">
                <div className="placeholder-glow">
                    <h1 className="display-1 fw-bolder my-5 mx-2 placeholder col-6"></h1>
                    <div className="row g-5">
                        <div className="col-md-8">
                            <div className="bg-primary bg-gradient border border-3 border-primary-subtle rounded-5 py-4 px-3">
                                <div className="placeholder rounded w-100 mb-4" style={{ height: 400 }}></div>
                                <div className="d-flex gap-3">
                                    {[...Array(5)].map((_, i) => (
                                        <div key={i} className="placeholder rounded" style={{ width: 100, height: 75 }}></div>
                                    ))}
                                </div>
                            </div>
                        </div>
                        <div className="col-md-4 d-flex flex-column gap-4">
                            <div className="d-flex flex-column align-items-center bg-success bg-gradient border border-5 rounded-5 border-success-subtle p-3">
                                <span className="placeholder col-9 display-2 d-block mb-3"></span>
                                <div className="btn btn-outline-light bg-gradient disabled placeholder col-8"></div>
                            </div>

                            <div className="d-flex flex-column align-items-center bg-primary bg-gradient border border-5 rounded-5 border-primary-subtle p-3">
                                <span className="placeholder col-9 display-2 d-block mb-3"></span>
                                <div className="placeholder w-100 mb-3"></div>
                                <div className="btn btn-outline-light bg-gradient disabled placeholder col-8"></div>
                            </div>
                        </div>
                    </div>

                    <div className="my-4">
                        <span className="placeholder col-3 display-6"></span>
                    </div>

                    <div className="bg-secondary-subtle border border-3 rounded-3 px-5 my-3 p-3">
                        <div className="d-flex align-items-center mb-4">
                            <div className="placeholder rounded-circle" style={{ width: 90, height: 90 }}></div>
                            <span className="placeholder display-3 ms-3 col-4"></span>
                        </div>
                        <div className="placeholder-glow d-flex flex-wrap gap-1">
                            <div className="placeholder col-4 mb-2"></div>
                            <div className="placeholder col-5 mb-2"></div>
                            <div className="placeholder col-2 mb-2"></div>
                            <div className="placeholder col-7 mb-2"></div>
                            <div className="placeholder col-4 mb-2"></div>
                            <div className="placeholder col-6 mb-2"></div>
                            <div className="placeholder col-4 mb-2"></div>
                            <div className="placeholder col-4 mb-2"></div>
                            <div className="placeholder col-4 mb-2"></div>
                        </div>
                    </div>

                    <div className="my-4">
                        <h2 className="display-3 fw-medium mb-3 ms-3 placeholder col-3"></h2>
                        <ul className="list-group">
                            {[...Array(2)].map((_, index) => (
                                <BidListPlaceHolder key={index} displaySize={3} imageSize={40} isRounded={true} />
                            ))}
                        </ul>
                    </div>
                </div>
            </main>
            :
            <main className="container">
                <h1 className="display-1 fw-bolder my-5 mx-2">{auction.title}</h1>
                <div className="row g-5">
                    <div className="col-md-8 bg-primary bg-gradient border border-3 border-primary-subtle rounded-5 p-3">
                        <div className="row justify-content-center px-3">
                            {auction.pictures.slice(0, 1).map((picture, index) =>
                                <button type="button" key={index} className="btn p-0 border-0 bg-transparent w-auto"
                                    data-bs-toggle="modal" data-bs-target="#pictureModal" onClick={() => setSlide(index)}>
                                    <img src={picture} className="img-fluid rounded-3" alt={"" + index} />
                                </button>
                            )}
                        </div>
                        <div className="row d-flex gap-3 justify-content-around">
                            {auction.pictures.slice(1).map((picture, index) =>
                                <button type="button" key={index + 1} className="btn p-0 border-0 bg-transparent w-auto mt-3"
                                    data-bs-toggle="modal" data-bs-target="#pictureModal" onClick={() => setSlide(index + 1)}>
                                    <img src={picture} width='100' className="img-fluid" alt={"" + (index + 1)} />
                                </button>
                            )}
                        </div>
                    </div>
                    <div className="col-md-4 d-flex justify-content-center">
                        {auction.createdBy.id !== loggedInUser?.id &&
                        (new Date(auction.startDate) < new Date() && new Date(auction.endDate) > new Date() && !auction.winner)
                            ?
                            <div className="d-flex flex-wrap align-content-around align-items-around">
                                {(!auction.isSecret && auction.buyNowPrice) &&
                                    <>
                                        <div className="d-flex vstack align-items-center main-color bg-gradient border border-5 rounded-5 border-success-subtle p-3">
                                            <label className="form-label display-2 fw-bold">{auction.buyNowPrice}$</label>
                                            <button className="btn btn-lg btn-outline-light bg-gradient shadow-lg" onClick={handleBuyNow}>
                                                <span className="fw-bold h3">Buy Now!</span>
                                            </button>
                                        </div>
                                        {errors.bid && <p className="text-danger">{errors.bid}</p>}
                                    </>
                                }

                                {auction.isSecret
                                    ?
                                    <>
                                        <div className="d-flex vstack align-items-center bg-primary bg-gradient border border-5 rounded-5 border-primary-subtle p-3">
                                            <input type="number" id="startingPrice" className="form-control mb-3"
                                                min={auction.nextPrice} value={bidPrice} onChange={(e) => setBidPrice(e.target.value)} />
                                            <button className="btn btn-lg btn-outline-light bg-gradient shadow-lg" onClick={handleBid}>
                                                <span className="fw-bold h3">Bid On Auction</span>
                                            </button>
                                        </div>
                                        {errors.bid && <p className="text-danger">{errors.bid}</p>}
                                    </>
                                    :
                                    <>
                                        <div className="d-flex vstack align-items-center bg-primary bg-gradient border border-5 rounded-5 border-primary-subtle p-3">
                                            <label htmlFor="startingPriceRange" className="form-label display-2 fw-bold">{bidPrice}$</label>
                                            <input type="range" id="startingPriceRange" className="form-range mb-3"
                                                min={auction.nextPrice} step={auction.bidIncrement ? auction.bidIncrement : 1}
                                                max={auction.buyNowPrice} value={bidPrice} onChange={(e) => setBidPrice(e.target.value)} />
                                            <button className="btn btn-lg btn-outline-light bg-gradient shadow-lg" onClick={handleBid}>
                                                <span className="fw-bold h3">Bid On Auction</span>
                                            </button>
                                        </div>
                                        {errors.buyNow && <p className="text-danger">{errors.buyNow}</p>}
                                    </>
                                }
                            </div>
                            : auction.winner &&
                            <div className="d-flex flex-column align-items-center justify-content-center">
                                <div className="display-6 fw-bold mb-4 text-center">The winner:</div>
                                <ProfilePicture pictureUrl={auction.winner.profilePicture} rounded={true} size={150} />
                                <div className="display-6 fw-medium">{nameShower(auction.winner)}</div>
                            </div>
                        }
                    </div>
                </div>
                <div className="row">
                    <h3 className="display-6">{dateToString(auction.startDate)} - {dateToString(auction.endDate)}</h3>
                </div>
                <div className="bg-secondary-subtle border border-3 rounded-3 px-5 my-3 p-3">
                    <Link className="d-flex align-content-center mb-4" to={`../users/${auction.createdBy.id}`}>
                        <ProfilePicture pictureUrl={auction.createdBy.profilePicture} rounded={true} size={90} />
                        <div className="display-3 fw-bold ms-3">{nameShower(auction.createdBy)}</div>
                    </Link>
                    <div className="row container">
                        <div className="lead">
                            {auction.description}
                        </div>
                    </div>
                </div>
                {!auction.isSecret &&
                    <>
                        <div className="row">
                            <hr className="w-100" />
                        </div>
                        <div className="row mb-5">
                            <h2 className="display-3 fw-medium ms-3 ">History</h2>
                            <ul className="list-group">
                                {auction.bids.length === 0
                                    ?
                                    <NoComponent type="bids" size={100} />
                                    :
                                    auction.bids.reverse().map((bid, index) =>
                                        <li key={index} className={`list-group-item ${bid.user.id === loggedInUser?.id && "main-color border border-3 border-black"}
                                            d-flex align-items-center justify-content-between`}>
                                            <Link className="d-flex align-items-center" to={`../users/${bid.user.id}`}>
                                                <ProfilePicture pictureUrl={bid.user.profilePicture} rounded={true} size={40} />
                                                <h2 className="display-6 fw-bold ms-2">{nameShower(bid.user)}</h2>
                                            </Link>
                                            <div className="h1"><span className="badge bg-primary rounded-pill">{bid.amount}$</span></div>
                                        </li>
                                    )}
                            </ul>
                        </div>
                    </>
                }

                <div className="modal fade" id="pictureModal" aria-labelledby="modalLabel" aria-hidden="true">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <ModalHeader>Pictures</ModalHeader>
                            <div className="modal-body bg-black p-0">
                                <div id="auctionItemCarousel" className="carousel slide" data-bs-ride="carousel">
                                    <div className="carousel-indicators">
                                        {auction.pictures.map((_, index) =>
                                            <button type="button" data-bs-target="#auctionItemCarousel" key={index}
                                                className={`${slide === index ? "active" : ""}`} aria-current={slide === index}
                                                data-bs-slide-to={index} aria-label={`Slide ${index + 1}`}></button>
                                        )}
                                    </div>
                                    <div className="carousel-inner align-content-center" style={{ height: 350 }}>
                                        {auction.pictures.map((picture, index) =>
                                            <div key={index} className={`carousel-item ${slide === index && "active"}`}
                                                data-bs-interval="3000" >
                                                <img src={picture} className="w-100" alt={index + ""} />
                                            </div>
                                        )}
                                    </div>
                                    <button className="carousel-control-prev" type="button" data-bs-target="#auctionItemCarousel" data-bs-slide="prev">
                                        <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                                        <span className="visually-hidden">Previous</span>
                                    </button>
                                    <button className="carousel-control-next" type="button" data-bs-target="#auctionItemCarousel" data-bs-slide="next">
                                        <span className="carousel-control-next-icon" aria-hidden="true"></span>
                                        <span className="visually-hidden">Next</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
    );
}
