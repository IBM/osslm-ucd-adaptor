package com.accantosystems.stratoss.driver.ucd.model.ucd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//{
//    "OS-FLV-DISABLED:disabled": false,
//    "OS-FLV-EXT-DATA:ephemeral": 0,
//    "disk": "80",
//    "id": "m1.large",
//    "links": [
//        {
//            "href": "http://cloud.example.com:8774/v2/abc/flavors/4",
//            "rel": "self",
//            "type": null
//        },
//        {
//            "href": "http://cloud.example.com:8774/v2/abc/flavors/4",
//            "rel": "bookmark",
//            "type": null
//        }
//    ],
//    "name": "m1.large",
//    "os-flavor-access:is_public": true,
//    "public": true,
//    "ram": 8192,
//    "rxtx_cap": null,
//    "rxtx_factor": 1,
//    "rxtx_quota": null,
//    "swap": "",
//    "vcpus": "4"
//}

@JsonIgnoreProperties(ignoreUnknown=true)
public class Flavor {

	private String id;
	private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{\"_class\":\"Flavor\", \"id\":\"%s\", \"name\":\"%s\"}", id, name);
	}
	
}
