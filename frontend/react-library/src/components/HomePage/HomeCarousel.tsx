export const HomeCarousel = () => {
    return (
        <div className="container p-5">
            <div id="homeCarousel" className="carousel slide" data-bs-ride="carousel">
                <div className="carousel-indicators">
                    <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="0" className="active" aria-current="true" aria-label="Slide 1"></button>
                    <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="1" aria-label="Slide 2"></button>
                    <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="2" aria-label="Slide 3"></button>
                    <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="3" aria-label="Slide 4"></button>
                    <button type="button" data-bs-target="#homeCarousel" data-bs-slide-to="4" aria-label="Slide 5"></button>
                </div>
                <div className="carousel-inner">
                    <div className="carousel-item active" data-bs-interval="3000">
                        <img src={require('./../../images/auctions.jpeg')} className="d-block w-100 h-50" alt="1" />
                        <div className="carousel-caption d-none d-md-block text-bg-dark bg-opacity-25">
                            <h5>Create auctions!</h5>
                            <p>Create auctions to sell your products!</p>
                        </div>
                    </div>
                    <div className="carousel-item" data-bs-interval="3000">
                        <img src={require('./../../images/bid.jpeg')} className="d-block w-100 h-50" alt="2" />
                        <div className="carousel-caption d-none d-md-block text-bg-dark bg-opacity-25">
                            <h5>Bid!</h5>
                            <p>Bid in some auctions to earn some valuable items!</p>
                        </div>
                    </div>
                    <div className="carousel-item" data-bs-interval="3000">
                        <img src={require('./../../images/security.jpeg')} className="d-block w-100 h-50" alt="3" />
                        <div className="carousel-caption d-none d-md-block text-bg-dark bg-opacity-25">
                            <h5>Public vs. secret auctions</h5>
                            <p>Choose between public or secret auctions by deciding wether you want the bids to be seen for the public or not!</p>
                        </div>
                    </div>
                    <div className="carousel-item" data-bs-interval="3000">
                        <img src={require('./../../images/follow.jpeg')} className="d-block w-100 h-50" alt="3" />
                        <div className="carousel-caption d-none d-md-block text-bg-dark bg-opacity-25">
                            <h5>Follow users!</h5>
                            <p>Follow users to stay up to date with the news.</p>
                        </div>
                    </div>
                    <div className="carousel-item" data-bs-interval="3000">
                        <img src={require('./../../images/review.jpeg')} className="d-block w-100 h-50" alt="3" />
                        <div className="carousel-caption d-none d-md-block text-bg-dark bg-opacity-25">
                            <h5>Leave a review!</h5>
                            <p>Leave a review to make others know the users's reliability!</p>
                        </div>
                    </div>
                </div>
                <button className="carousel-control-prev" type="button" data-bs-target="#homeCarousel" data-bs-slide="prev">
                    <span className="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Previous</span>
                </button>
                <button className="carousel-control-next" type="button" data-bs-target="#homeCarousel" data-bs-slide="next">
                    <span className="carousel-control-next-icon" aria-hidden="true"></span>
                    <span className="visually-hidden">Next</span>
                </button>
            </div>
        </div>
    );
} 