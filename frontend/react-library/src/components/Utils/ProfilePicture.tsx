import { getTransformedImageUrl } from "./helper";

export const ProfilePicture: React.FC<{
    pictureUrl: string | undefined,
    rounded: boolean,
    size: number
}> = (props) => {
    return (
        props.pictureUrl
            ?
            props.rounded
                ?
                <img src={getTransformedImageUrl(props.pictureUrl, 1000, 1000, true)} width={props.size} height={props.size} className="rounded-circle" alt="1" />
                :
                <img src={props.pictureUrl} className="w-100 h-100" alt="1" />
            :
            props.rounded
                ?
                <div className="rounded-circle">
                    <svg xmlns="http://www.w3.org/2000/svg" width={props.size} height={props.size} fill="currentColor" className="bi bi-person-circle rounded-circle text-black bg-success" viewBox="0 0 16 16">
                        <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0" />
                        <path fillRule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1" />
                    </svg>
                </div>
                :
                <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="bi bi-person-fill text-black bg-success w-100 h-100" viewBox="0 0 16 16">
                    <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6" />
                </svg>
    );
}