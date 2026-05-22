package br.com.fiap.api_gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@RestController
public class GatewayController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.upload-service}")
    private String uploadServiceUrl;

    @Value("${services.report-service}")
    private String reportServiceUrl;

    @PostMapping("/analyses")
    public ResponseEntity<byte[]> proxyUploadPost(
            MultipartHttpServletRequest request) throws Exception {

        MultipartFile file = request.getFile("file");
        if (file == null) {
            return ResponseEntity.badRequest().build();
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        URI uri = new URI(uploadServiceUrl + "/analyses");

        return restTemplate.exchange(uri, HttpMethod.POST, entity, byte[].class);
    }

    @RequestMapping("/analyses/**")
    public ResponseEntity<byte[]> proxyUpload(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request) throws URISyntaxException {
        return forward(request, method, body, uploadServiceUrl);
    }

    @RequestMapping("/reports/**")
    public ResponseEntity<byte[]> proxyReport(
            @RequestBody(required = false) byte[] body,
            HttpMethod method,
            HttpServletRequest request) throws URISyntaxException {
        return forward(request, method, body, reportServiceUrl);
    }

    private ResponseEntity<byte[]> forward(HttpServletRequest request,
                                           HttpMethod method,
                                           byte[] body,
                                           String targetUrl) throws URISyntaxException {
        URI uri = new URI(targetUrl + request.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.set(name, request.getHeader(name));
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(uri, method, entity, byte[].class);
    }
}