<?xml version="1.0" encoding="GBK"?>
<Packet type="REQUEST" version="1.0">
    <Head>
        <RequestType>${pltHeadDto.requestType!}</RequestType>
        <User>${pltHeadDto.user!}</User>
        <Password>${pltHeadDto.password!}</Password>
    </Head>
    <Body>
        <BasePart>
            <#-- 投保查询校验类型 -->
            <CheckType>2</CheckType>
        </BasePart>
        <Vehicle>
            <#-- 号牌号码 -->
            <LicensePlateNo>${pltCarDto.licensePlateNo!}</LicensePlateNo>
            <#-- 号牌底色 -->
            <LicensePlateColorCode>${pltCarDto.licenseColorCode!}</LicensePlateColorCode>
            <#-- 号牌种类代码 -->
            <LicensePlateType>${pltCarDto.licenseType!}</LicensePlateType>
            <#-- 车辆种类代码 -->
            <MotorTypeCode>${pltCarDto.carKindCode!}</MotorTypeCode>
            <#-- 使用性质代码 -->
            <MotorUsageTypeCode>${pltCarDto.carUserNatureCode!}</MotorUsageTypeCode>
            <#-- 车辆初始登记日期 -->
            <FirstRegisterDate>${pltCarDto.enrollDate!}</FirstRegisterDate>
            <#-- 车辆识别代号（车架号/VIN码） -->
            <VIN>${pltCarDto.frameNo!}</VIN>
            <#-- 发动机号 -->
            <EngineNo>${pltCarDto.engineNo!}</EngineNo>
        </Vehicle>
    </Body>
</Packet>
