package elte.icj06o.auction.controller;

import elte.icj06o.auction.model.Country;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @GetMapping
    public List<Map<String, String>> getAllCountries() {
        return Arrays.stream(Country.values())
                .map(c -> Map.of(
                        "code", c.name(),
                        "name", c.getDisplayName()
                ))
                .collect(Collectors.toList());
    }
}
