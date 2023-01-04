package com.chaechae.realworldspringboot.security.jwt.filter;

import com.chaechae.realworldspringboot.base.exception.RealWorldException;
import com.chaechae.realworldspringboot.base.exception.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RealWorldException e) {
            setDefaultResponse(response);
            objectMapper.writeValue(response.getWriter(), setRealWorldErrorResponse(e));
        } catch (JwtException e) {
            setDefaultResponse(response);
            objectMapper.writeValue(response.getWriter(), setErrorResponse(e));
        } catch (RuntimeException e) {
            setDefaultResponse(response);
            objectMapper.writeValue(response.getWriter(), setErrorResponse(e));
        }
    }

    private ErrorResponse setErrorResponse(Exception e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(e.getMessage())
                .build();
    }

    private ErrorResponse setRealWorldErrorResponse(RealWorldException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(e.getExceptionType().getMessage())
                .validation(e.getValidation())
                .build();
    }

    private void setDefaultResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }
}
