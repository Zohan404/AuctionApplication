import React from "react";

export const AuctionCardPlaceHolder: React.FC = () => {
    return (
        <div className="col">
            <div className="card shadow h-100" aria-hidden="true">
                <div className="placeholder col-12">
                    <div className="opacity-0">1</div>
                    <div className="opacity-0">2</div>
                    <div className="opacity-0">3</div>
                    <div className="opacity-0">4</div>
                    <div className="opacity-0">5</div>
                    <div className="opacity-0">6</div>
                    <div className="opacity-0">7</div>
                </div>
                <div className="card-body">
                    <div className="row">
                        <div className="col-8">
                            <h5 className="card-title placeholder-glow">
                                <span className="placeholder col-6"></span>
                            </h5>
                        </div>
                        <div className="col-4">
                            <p className="card-text placeholder-glow d-flex justify-content-end">
                                <span className="placeholder col-6"></span>
                            </p>
                        </div>
                    </div>
                    <p className="card-text placeholder-glow d-flex flex-wrap gap-1">
                        <span className="placeholder col-3"></span>
                        <span className="placeholder col-3"></span>
                        <span className="placeholder col-8"></span>
                        <span className="placeholder col-10"></span>
                    </p>
                </div>
                <div className="card-footer text-center">
                    <p className="card-text placeholder-glow">
                        <span className="placeholder col-3"></span>
                    </p>
                </div>
            </div>
        </div>
    );
}