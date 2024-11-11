package com.javarush.quest.shubchynskyi.exception.handlers;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.javarush.quest.shubchynskyi.constant.Key.ERROR;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.ID_NOT_FOUND_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@Import(GlobalExceptionHandlerIT.TestController.class)
public class GlobalExceptionHandlerIT {

    @Autowired
    private MockMvc mockMvc;

    @Controller
    static class TestController {

        @GetMapping("/test/number-format-exception")
        public String triggerNumberFormatException() {
            throw new NumberFormatException();
        }

        @GetMapping("/test/method-argument-type-mismatch")
        public String triggerMethodArgumentTypeMismatch(@RequestParam("number") Integer number) {
            return INDEX + number;
        }

        @GetMapping("/test/general-exception")
        public String triggerGeneralException() throws Exception {
            throw new Exception();
        }
    }

    @Test
    @DisplayName("Handle NumberFormatException and redirect to index with error message")
    public void testHandleNumberFormatException() throws Exception {
        mockMvc.perform(get("/test/number-format-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(ID_NOT_FOUND_ERROR)));
    }

    @Test
    @DisplayName("Handle MethodArgumentTypeMismatchException and redirect to index with error message")
    public void testHandleMethodArgumentTypeMismatchException() throws Exception {
        mockMvc.perform(get("/test/method-argument-type-mismatch")
                        .param("number", "notANumber"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(ID_NOT_FOUND_ERROR)));
    }

    @Test
    @DisplayName("Handle general exception and redirect to index with error message")
    public void testHandleGeneralException() throws Exception {
        mockMvc.perform(get("/test/general-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR)));
    }
}