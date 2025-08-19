import { useState } from "react";
import { Link } from "react-router-dom";
import { ListedAuctionModel } from "../Models/ListedAuctionModel";
import { nameShower } from "../Utils/helper";
import { ProfilePicture } from "../Utils/ProfilePicture";
import { MainAuctionCard } from "./MainAuctionCard";

export const AuctionCard: React.FC<{
    auction?: ListedAuctionModel
}> = (props) => {
    const [overlayClosed, setoverlayClosed] = useState<boolean>(false);

    return (
        <div className="col">
            {props.auction === null || props.auction === undefined
                ?
                <Link className="card btn btn-link hover bg-secondary link-opacity-10-hover shadow h-100 w-100" to="/auctions/new" style={{ minHeight: 350}}>
                    <div className="card-body d-flex flex-column justify-content-center align-items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" fill="currentColor" className="bi bi-plus-square" viewBox="0 0 16 16">
                            <path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2z" />
                            <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4" />
                        </svg>
                    </div>
                </Link>
                :
                new Date(props.auction.startDate) > new Date() ?
                    <div className="position-relative h-100" >
                        <div className="h-100" style={!overlayClosed ? { filter: "blur(3px)" } : undefined}>
                            <MainAuctionCard auction={props.auction} />
                        </div>
                        <div className={`position-absolute top-0 start-0 w-100 h-100 d-flex flex-column
                            align-items-center justify-content-center bg-dark bg-opacity-50 rounded-2 text-white
                            ${overlayClosed && "d-none"}`}>
                            <div className="d-flex align-items-center mb-2">
                                <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" className="bi bi-lock-fill" viewBox="0 0 16 16">
                                    <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2m3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2" />
                                </svg>
                                <div className="h5 m-0">Locked</div>
                            </div>
                            <div className="lean px-3 text-center mb-2">The auction has not yet been started. Do you want to preview it?</div>
                            <button className="btn btn-primary" onClick={() => { setoverlayClosed(true) }}>Preview</button>
                        </div>
                    </div>
                    :
                    new Date(props.auction.endDate) < new Date() || props.auction.winner ?
                        <div className="position-relative h-100" >
                            <div className="h-100" style={!overlayClosed ? { filter: "blur(3px)" } : undefined}>
                                <MainAuctionCard auction={props.auction} />
                            </div>
                            <div className={`position-absolute top-0 start-0 w-100 h-100 d-flex flex-column
                                align-items-center justify-content-center bg-dark bg-opacity-50 rounded-2 text-white
                                ${overlayClosed && "d-none"}`}>
                                <div className="d-flex align-items-center mb-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" fill="currentColor" className="bi bi-hourglass-bottom" viewBox="0 0 16 16">
                                        <path d="M2 1.5a.5.5 0 0 1 .5-.5h11a.5.5 0 0 1 0 1h-1v1a4.5 4.5 0 0 1-2.557 4.06c-.29.139-.443.377-.443.59v.7c0 .213.154.451.443.59A4.5 4.5 0 0 1 12.5 13v1h1a.5.5 0 0 1 0 1h-11a.5.5 0 1 1 0-1h1v-1a4.5 4.5 0 0 1 2.557-4.06c.29-.139.443-.377.443-.59v-.7c0-.213-.154-.451-.443-.59A4.5 4.5 0 0 1 3.5 3V2h-1a.5.5 0 0 1-.5-.5m2.5.5v1a3.5 3.5 0 0 0 1.989 3.158c.533.256 1.011.791 1.011 1.491v.702s.18.149.5.149.5-.15.5-.15v-.7c0-.701.478-1.236 1.011-1.492A3.5 3.5 0 0 0 11.5 3V2z" />
                                    </svg>
                                    <div className="h5 m-0">Ended</div>
                                </div>
                                {props.auction.winner !== null && props.auction.winner !== undefined
                                    ?
                                    <>
                                        <div className="lean px-3 text-center mb-2">The auction has ended. The winner is: </div>
                                        <div className="d-flex align-items-center mb-2">
                                            <ProfilePicture pictureUrl={props.auction.winner?.profilePicture} rounded={true} size={70} />
                                            <div className="h3 fw-bold ms-2">
                                                {nameShower(props.auction.winner)}
                                            </div>
                                        </div>
                                    </>
                                    :
                                    <div className="lean px-3 text-center mb-2">
                                        The time is up! The auction has ended. Noone have become the winner.
                                    </div>
                                }

                                <button className="btn btn-primary" onClick={() => { setoverlayClosed(true) }}>Check</button>
                            </div>
                        </div>
                        :
                        <MainAuctionCard auction={props.auction} />
            }
        </div>
    );
}