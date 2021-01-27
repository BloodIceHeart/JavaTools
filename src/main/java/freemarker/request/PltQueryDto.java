package freemarker.request;

import java.io.Serializable;

/**
 * 平台交互对象
 */
public class PltQueryDto implements Serializable {
    private PltHeadDto pltHeadDto;
    private PltCarDto pltCarDto;

    public PltHeadDto getPltHeadDto() {
        return pltHeadDto;
    }

    public void setPltHeadDto(PltHeadDto pltHeadDto) {
        this.pltHeadDto = pltHeadDto;
    }

    public PltCarDto getPltCarDto() {
        return pltCarDto;
    }

    public void setPltCarDto(PltCarDto pltCarDto) {
        this.pltCarDto = pltCarDto;
    }
}
