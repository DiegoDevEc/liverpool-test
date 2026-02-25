package com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex;

import com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto.CopomexResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "copomex-client", url = "${copomex.api.url}")
public interface CopomexClient {

    @GetMapping("/query/info_cp/{cp}")
    List<CopomexResponse> getInfoByCp(
            @PathVariable("cp") String cp,
            @RequestParam("token") String token);
}
