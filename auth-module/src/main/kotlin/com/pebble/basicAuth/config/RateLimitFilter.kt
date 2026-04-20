package com.pebble.basicAuth.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * [Phase 4: Filtering Filter - Refactored]
 * ë¹„ì •???¸ë˜?½ì„ ê°ì??˜ê³  ì°¨ë‹¨?˜ëŠ” 'ê´€ë¬? ?„í„°?…ë‹ˆ??
 * ë¹„ì¦ˆ?ˆìŠ¤ ë¡œì§(Bucket ?ì„± ???€ RateLimitService???„ì„?ˆìŠµ?ˆë‹¤.
 */
@Component
class RateLimitFilter(
    private val rateLimitService: RateLimitService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val clientIp = extractClientIp(request)
        
        // 1. ?œë¹„?¤ì— ? í° ?Œëª¨ë¥??”ì²­?˜ì—¬ ?¸ë˜???ˆìš© ?¬ë? ê²°ì •
        if (rateLimitService.tryConsume(clientIp)) {
            // [?•ìƒ]: ?¤ìŒ ?„í„°ë¡?ì§„í–‰
            chain.doFilter(request, response)
        } else {
            // [ë¹„ì •??: 429 ?ëŸ¬ ë°˜í™˜ ë°?ì¦‰ì‹œ ì°¨ë‹¨
            sendErrorResponse(response)
        }
    }

    /**
     * ?´ë¼?´ì–¸??IP ?ë³„ (X-Forwarded-For ?¤ë” ?°ì„  ?•ì¸)
     */
    private fun extractClientIp(request: HttpServletRequest): String {
        return request.getHeader("X-Forwarded-For")?.split(",")?.get(0) 
            ?: request.remoteAddr
    }

    /**
     * ?œì??”ëœ ?ëŸ¬ ?‘ë‹µ ?„ì†¡
     */
    private fun sendErrorResponse(response: HttpServletResponse) {
        response.apply {
            status = HttpStatus.TOO_MANY_REQUESTS.value()
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write("""
                {
                    "status": 429,
                    "error": "Too Many Requests",
                    "message": "?”ì²­???ˆë¬´ ë§ìŠµ?ˆë‹¤. ? ì‹œ ???¤ì‹œ ?œë„?´ì£¼?¸ìš”."
                }
            """.trimIndent())
        }
    }
}
