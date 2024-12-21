package com.javarush.quest.shubchynskyi.integration.exception.handlers;

import com.javarush.quest.shubchynskyi.localization.ErrorLocalizer;
import com.javarush.quest.shubchynskyi.test_config.ConfigIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.javarush.quest.shubchynskyi.constant.Key.ERROR;
import static com.javarush.quest.shubchynskyi.constant.Key.ID;
import static com.javarush.quest.shubchynskyi.constant.Route.INDEX;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.ID_NOT_FOUND_ERROR;
import static com.javarush.quest.shubchynskyi.localization.ViewErrorMessages.UNEXPECTED_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ConfigIT
@Import(GlobalExceptionHandlerIT.TestController.class)
public class GlobalExceptionHandlerIT {

    @Value("${invalid.quest.incorrectId}")
    public String incorrectId;

    @Autowired
    private MockMvc mockMvc;

    @Controller
    static class TestController {

        @GetMapping("/test/number-format-exception")
        public String triggerNumberFormatException() {
            throw new NumberFormatException();
        }

        @GetMapping("/test/method-argument-type-mismatch")
        public String triggerMethodArgumentTypeMismatch(@RequestParam(ID) Long id) {
            return INDEX + id;
        }

        @GetMapping("/test/general-exception")
        public String triggerGeneralException() throws Exception {
            throw new Exception();
        }
    }

    @Test
    void whenNumberFormatExceptionOccurs_thenRedirectToIndexWithErrorMessage() throws Exception {
        mockMvc.perform(get("/test/number-format-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(ID_NOT_FOUND_ERROR)));
    }

    @Test
    void whenMethodArgumentTypeMismatchOccurs_thenRedirectToIndexWithErrorMessage() throws Exception {
        mockMvc.perform(get("/test/method-argument-type-mismatch")
                        .param(ID, incorrectId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(ID_NOT_FOUND_ERROR)));
    }

    @Test
    void whenGeneralExceptionOccurs_thenRedirectToIndexWithErrorMessage() throws Exception {
        mockMvc.perform(get("/test/general-exception"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(INDEX))
                .andExpect(flash().attribute(ERROR, ErrorLocalizer.getLocalizedMessage(UNEXPECTED_ERROR)));
    }
}