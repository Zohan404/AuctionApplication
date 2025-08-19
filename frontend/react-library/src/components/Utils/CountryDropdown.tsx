import { useEffect, useState } from "react";

class Country {
    constructor(public code: string, public name: string) { }
}

export const CountryDropdown: React.FC<{
    buttonColor: string,
    onSelected: (code: string) => void,
    noCountryText?: string,
    initialCountry?: string
}> = (props) => {
    const [countries, setCountries] = useState<Country[]>([]);
    const [selectedCountry, setSelectedCountry] = useState<Country>();

    const handleSelect = (country: Country) => {
        props.onSelected(country.code);
        setSelectedCountry(country);
    };

    useEffect(() => {
        fetch("http://localhost:8081/api/countries")
            .then(res => res.json())
            .then(data => {
                setCountries(data);
                setSelectedCountry(countries[0]);

                if (props.initialCountry !== null) {
                    const match: Country = data.find((country: Country) => country.name === props.initialCountry);
                    if (match) setSelectedCountry(match)
                }
            });
    }, []);

    return (
        <div className="dropdown is-invalid">
            <button className={`btn ${props.buttonColor} dropdown-toggle d-flex justify-content-between align-items-center w-100`}
                id="location" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                {(selectedCountry && selectedCountry.code !== "NO_COUNTRY") ?
                    <span className="d-flex align-items-center gap-2">
                        <img src={require(`../../images/countries/${selectedCountry.code}.png`)}
                            alt={selectedCountry.name} width="24" />
                        {selectedCountry.name}
                    </span>
                    :
                    <span className={`${!props.noCountryText && "invisible"}`}>
                        {props.noCountryText ? props.noCountryText : "Nothing"}
                    </span>
                }
            </button>
            <ul className="dropdown-menu w-100" style={{ maxHeight: "300px", overflowY: "auto" }}>
                {countries.map((country) => (
                    <li key={country.code}>
                        <button type="button" className="dropdown-item d-flex align-items-center gap-2"
                            onClick={() => handleSelect(country)}>
                            {country.code === "NO_COUNTRY"
                                ?
                                <div className={`${!props.noCountryText && "invisible"}`}>
                                    {props.noCountryText ? props.noCountryText : "Nothing"}
                                </div>
                                :
                                <>
                                    <img src={require(`../../images/countries/${country.code}.png`)}
                                        alt={country.name} width="20" />
                                    {country.name}
                                </>
                            }
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}