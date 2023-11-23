package com.smile.apiobjects.address;

import com.smile.apiobjects.IPayload;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.json.JSONObject;

// @formatter:off
/**
 * Payload Example:
 * {
 *   "phone": "705-670-0457",
 *   "fullAddress": "455 Bo Bypass, North Dellamouth, ND 09640"
 * }
 */
// @formatter:on
@Data
@Builder
@Accessors(chain = true)
public class AddressPayloadGenerator implements IPayload {
    private Integer id;
    private String fullAddress;
    private String phone;
    private Boolean isDefault;

    @Override
    public String buildPayload() {
        return new JSONObject(this).toString();
    }
}
