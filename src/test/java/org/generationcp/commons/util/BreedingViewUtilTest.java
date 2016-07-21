package org.generationcp.commons.util;

import junit.framework.Assert;
import org.junit.Test;

public class BreedingViewUtilTest {

	public static final String STRING_WITH_SPECIAL_CHARACTERS = " ABC\\1~2!3@4#5$6%7^8&9*0(a)b_c+d{e}f|g:h\"i<j>k?l`m-n=o[p]q;r's,t.u/v ";

	@Test
	public void testSanitizeName() {

		final String result = BreedingViewUtil.sanitizeName(STRING_WITH_SPECIAL_CHARACTERS);

		Assert.assertEquals("All characters except alphanumeric, dash, percentage and apostrophe should be replaced with an underscore.",
				"_ABC_1_2_3_4_5_6%7_8_9_0_a_b_c_d_e_f_g_h_i_j_k_l_m-n_o_p_q_r's_t_u_v_", result);

	}

	@Test
	public void testTrimAndSanitizeName() {

		final String result = BreedingViewUtil.trimAndSanitizeName(STRING_WITH_SPECIAL_CHARACTERS);

		Assert.assertEquals(
				"All characters except alphanumeric, dash, percentage and apostrophe should be replaced with an underscore. The leading and trailing spaces are removed.",
				"ABC_1_2_3_4_5_6%7_8_9_0_a_b_c_d_e_f_g_h_i_j_k_l_m-n_o_p_q_r's_t_u_v", result);

	}

	@Test
	public void testSanitizeNameAlphaNumericOnly() {

		final String result = BreedingViewUtil.sanitizeNameAlphaNumericOnly(STRING_WITH_SPECIAL_CHARACTERS);

		Assert.assertEquals("All characters except alphanumeric, dash and space should be replaced with an underscore.",
				" ABC_1_2_3_4_5_6_7_8_9_0_a_b_c_d_e_f_g_h_i_j_k_l_m-n_o_p_q_r_s_t_u_v ", result);

	}

}
