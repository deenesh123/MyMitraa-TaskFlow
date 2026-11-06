
package com.kelltontech.optima.api_service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelltontech.optima.api_domain.AdvanceSearchList;
import com.kelltontech.optima.api_domain.ChemicalTreatment;
import com.kelltontech.optima.api_domain.ConsolidatedDiluent;
import com.kelltontech.optima.api_domain.ConsolidatedDownTime;
import com.kelltontech.optima.api_domain.DailyChemicalSummary;
import com.kelltontech.optima.api_domain.DailyCrudeShipment;
import com.kelltontech.optima.api_domain.DailyEsp;
import com.kelltontech.optima.api_domain.DailyHPS;
import com.kelltontech.optima.api_domain.DailyHpUBH;
import com.kelltontech.optima.api_domain.DailyOilBalance;
import com.kelltontech.optima.api_domain.DailyOilForGeneration;
import com.kelltontech.optima.api_domain.DailyReinjection;
import com.kelltontech.optima.api_domain.DailyRestTanks;
import com.kelltontech.optima.api_domain.DailyTankAvailableStorage;
import com.kelltontech.optima.api_domain.DailyTankTotals;
import com.kelltontech.optima.api_domain.DailyTotalDiluentTankLevelAndFiscalization;
import com.kelltontech.optima.api_domain.DailyTotalTankAvailableStorage;
import com.kelltontech.optima.api_domain.DailyTotalWellPotentialAndProduction;
import com.kelltontech.optima.api_domain.DailyTotalmixtanklevelAndFiscalization;
import com.kelltontech.optima.api_domain.DailyWaterBalance;
import com.kelltontech.optima.api_domain.DailyWaterTank;
import com.kelltontech.optima.api_domain.DailyWellParameters;
import com.kelltontech.optima.api_domain.DailyWellPotential;
import com.kelltontech.optima.api_domain.DailyWellTest;
import com.kelltontech.optima.api_domain.DbDiluent;
import com.kelltontech.optima.api_domain.DbDiluentUnloadingAndFiscaliza;
import com.kelltontech.optima.api_domain.DbDilutentBarges;
import com.kelltontech.optima.api_domain.DbDownTime;
import com.kelltontech.optima.api_domain.DbHeavyFuelOil;
import com.kelltontech.optima.api_domain.DbLaboratory;
import com.kelltontech.optima.api_domain.DbOilBarges;
import com.kelltontech.optima.api_domain.DbUpdatesParameterDomain;
import com.kelltontech.optima.api_domain.DiluentTanks;
import com.kelltontech.optima.api_domain.DispatchTanks;
import com.kelltontech.optima.api_domain.DtCrudeShipmentAndGeneration;
import com.kelltontech.optima.api_domain.EmailRecipients;
import com.kelltontech.optima.api_domain.EspDiagnosticsData;
import com.kelltontech.optima.api_domain.ExtraTableChron;
import com.kelltontech.optima.api_domain.FilesDownloadedCountDomain;
import com.kelltontech.optima.api_domain.GlobalFileUploads;
import com.kelltontech.optima.api_domain.GlobalImportDropdownList;
import com.kelltontech.optima.api_domain.HourlyHpUBH;
import com.kelltontech.optima.api_domain.HourlyLaboratoryAnalysisASTM;
import com.kelltontech.optima.api_domain.HourlyLaboratoryWatercut;
import com.kelltontech.optima.api_domain.MonthlyProduction;
import com.kelltontech.optima.api_domain.OilLoadingandFiscalization;
import com.kelltontech.optima.api_domain.Production;
import com.kelltontech.optima.api_domain.Reinjection;
import com.kelltontech.optima.api_domain.Summary;
import com.kelltontech.optima.api_domain.embedded.ImportXlSheets;
import com.kelltontech.optima.api_errors.CustomExceptionCode;
import com.kelltontech.optima.api_errors.RegisteredException;
import com.kelltontech.optima.api_mv.EmailMV;
import com.kelltontech.optima.api_service.ChecimalTreatmentService;
import com.kelltontech.optima.api_service.CompletionFileservice;
import com.kelltontech.optima.api_service.ConsolidatedDiluentService;
import com.kelltontech.optima.api_service.ConsolidatedDownTimeService;
import com.kelltontech.optima.api_service.DailyBargeCrudeUploadDownloadService;
import com.kelltontech.optima.api_service.DailyChemicalSummaryService;
import com.kelltontech.optima.api_service.DailyCrudeShipmentService;
import com.kelltontech.optima.api_service.DailyDeferredProductionService;
import com.kelltontech.optima.api_service.DailyDiluentTanklevelAndFiscalizationService;
import com.kelltontech.optima.api_service.DailyEspService;
import com.kelltontech.optima.api_service.DailyHPSService;
import com.kelltontech.optima.api_service.DailyHpUBHService;
import com.kelltontech.optima.api_service.DailyMixTankLevelsAndFiscalizationService;
import com.kelltontech.optima.api_service.DailyOilBalanceService;
import com.kelltontech.optima.api_service.DailyOilForGenerationService;
import com.kelltontech.optima.api_service.DailyPlantTaskAndTimeService;
import com.kelltontech.optima.api_service.DailyReinjectionService;
import com.kelltontech.optima.api_service.DailyRestTanksService;
import com.kelltontech.optima.api_service.DailyTankAvailableStorageService;
import com.kelltontech.optima.api_service.DailyTankTotalService;
import com.kelltontech.optima.api_service.DailyTotalDiluentTankLevelAndFiscalizationService;
import com.kelltontech.optima.api_service.DailyTotalTankAvailableStorageService;
import com.kelltontech.optima.api_service.DailyTotalWellPotentialAndProductionService;
import com.kelltontech.optima.api_service.DailyTotalmixtanklevelAndFiscalizationService;
import com.kelltontech.optima.api_service.DailyWaterBalanceService;
import com.kelltontech.optima.api_service.DailyWaterTankService;
import com.kelltontech.optima.api_service.DailyWellParametersService;
import com.kelltontech.optima.api_service.DailyWellPotentialService;
import com.kelltontech.optima.api_service.DailyWellServiceService;
import com.kelltontech.optima.api_service.DailyWellTestService;
import com.kelltontech.optima.api_service.DbDiluentUnloadingAndFiscalizaService;
import com.kelltontech.optima.api_service.DbDilutentBargesService;
import com.kelltontech.optima.api_service.DbHeavyFuelOilService;
import com.kelltontech.optima.api_service.DbOilBargesService;
import com.kelltontech.optima.api_service.DiluentService;
import com.kelltontech.optima.api_service.DiluentTanksService;
import com.kelltontech.optima.api_service.DispatchTankReportedLevelsService;
import com.kelltontech.optima.api_service.DispatchTanksReportedService;
import com.kelltontech.optima.api_service.DispatchTanksService;
import com.kelltontech.optima.api_service.DowntimeService;
import com.kelltontech.optima.api_service.DtCrudeShipmentAndGenerationService;
import com.kelltontech.optima.api_service.EspDiagnosticsDataService;
import com.kelltontech.optima.api_service.FeignClientForDeviceManagementSystem;
import com.kelltontech.optima.api_service.Genericservices;
import com.kelltontech.optima.api_service.GlobalFileUploadsService;
import com.kelltontech.optima.api_service.HidrocarbonsService;
import com.kelltontech.optima.api_service.HourlyEspService;
import com.kelltontech.optima.api_service.HourlyHPSService;
import com.kelltontech.optima.api_service.HourlyHpUBHService;
import com.kelltontech.optima.api_service.HourlyLaboratoryAnalysisASTMService;
import com.kelltontech.optima.api_service.HourlyLaboratoryWaterCutSerivce;
import com.kelltontech.optima.api_service.HourlyReinjectionService;
import com.kelltontech.optima.api_service.HourlyWellTestService;
import com.kelltontech.optima.api_service.LaboratoryService;
import com.kelltontech.optima.api_service.OilLoadingandFiscalizationService;
import com.kelltontech.optima.api_service.ProductionService;
import com.kelltontech.optima.api_service.RealTimeSolidsMonitoringService;
import com.kelltontech.optima.api_service.ReinjectionService;
import com.kelltontech.optima.api_service.SummaryService;
import com.kelltontech.optima.api_successcode.CustomResponse;
import com.kelltontech.optima.api_successcode.SuccessCode;
import com.kelltontech.optima.api_util.StringUtil;
import com.kelltontech.optima.api_vm.GlobalFileUploadVM;
import com.kelltontech.optima.config.Constants;
import com.kelltontech.optima.repository.GlobalFileUploadsRepository;
import com.kelltontech.optima.security.CustomUserDetails;
import com.kelltontech.optima.web.rest.errors.CustomParameterizedException;
import com.kelltontech.optima.web.rest.vm.SearchVM;
import com.opencsv.CSVWriter;

@Service
public class GlobalFileUploadsServiceImpl implements GlobalFileUploadsService {
	private final Logger log = LoggerFactory.getLogger(GlobalFileUploadsServiceImpl.class);

	@Autowired
	private Environment environment;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	public MongoOperations mongoOperations;

	@Autowired
	private HourlyWellTestService hourlyWellTestServiceImpl;

	@Autowired
	private CompletionFileservice completionFileservice;

	@Autowired
	private DailyMixTankLevelsAndFiscalizationService dailyMixTankLevelsAndFiscalizationService;

	@Autowired
	private DailyDiluentTanklevelAndFiscalizationService dailyDiluentTanklevelAndFiscalizationService;

	@Autowired
	private DailyPlantTaskAndTimeService dailyPlantTaskAndTimeService;

	@Autowired
	private GlobalFileUploadsRepository globalFileUploadsRepository;

	@Autowired
	private HourlyHPSService hourlyHPSServiceImpl;

	@Autowired
	private DailyDeferredProductionService dailyDeferredProductionService;
	@Autowired
	private HourlyEspService espService;

	@Autowired
	private HourlyHpUBHService hourlyHpUBHService;

	@Autowired
	private DailyReinjectionService dailyReinjectionService;

	@Autowired
	private DailyWaterBalanceService dailyWaterBalanceService;

	@Autowired
	private DailyWaterTankService dailyWaterTankService;

	@Autowired
	private DailyTankAvailableStorageService dailyTankAvailableStorageService;

	@Autowired
	private HourlyLaboratoryAnalysisASTMService hourlyLaboratoryAnalysisASTMService;

	@Autowired
	private HourlyLaboratoryWaterCutSerivce hourlyLaboratoryWatercutSerivce;

	@Autowired
	private DailyEspService dailyEspService;

	@Autowired
	private DailyHpUBHService dailyHpUBHService;

	@Autowired
	private DailyHPSService dailyHPSService;

	@Autowired
	private DailyWellTestService dailyWellTestService;

	@Autowired
	private DailyWellParametersService dailyWellParametersService;

	@Autowired
	private DailyWellPotentialService dailyWellPotentialService;

	@Autowired
	private DailyTotalWellPotentialAndProductionService dailyTotalWellPotentialAndProductionService;

	@Autowired
	private DailyTotalmixtanklevelAndFiscalizationService dailyTotalmixtanklevelAndFiscalizationService;

	@Autowired
	private DispatchTanksService dispatchTanksService;

	@Autowired
	private DispatchTanksReportedService dispatchTanksReportedService;

	@Autowired
	private DailyTotalTankAvailableStorageService dailyTotalTankAvailableStorageService;

	@Autowired
	private DailyTotalDiluentTankLevelAndFiscalizationService dailyTotalDiluentTankLevelAndFiscalizationService;

	@Autowired
	private DiluentTanksService diluentTanksService;
	@Autowired
	private DailyRestTanksService dailyRestTanksService;

	@Autowired
	private DailyTankTotalService dailyTankTotalService;

	@Autowired
	private DailyCrudeShipmentService dailyCrudeShipmentService;

	@Autowired
	private DailyOilForGenerationService dailyOilForGenerationService;

	@Autowired
	private DtCrudeShipmentAndGenerationService dtCrudeShipmentAndGenerationService;

	@Autowired
	private DailyOilBalanceService dailyOilBalanceService;

	@Autowired
	private ProductionService productionService;

	@Autowired
	private ReinjectionService reinjectionService;

	@Autowired
	private OilLoadingandFiscalizationService oilLoadingandFiscalizationService;

	@Autowired
	private DbDiluentUnloadingAndFiscalizaService dbDiluentUnloadingAndFiscalizaService;

	@Autowired
	private DbHeavyFuelOilService dbHeavyFuelOilService;

	@Autowired
	private DbOilBargesService dbOilBargesService;

	@Autowired
	private DbDilutentBargesService dbDilutentBargesService;

	@Autowired
	private LaboratoryService laboratoryService;

	@Autowired
	private DiluentService diluentService;

	@Autowired
	private ConsolidatedDiluentService consolidatedDiluentService;

	@Autowired
	private DowntimeService downtimeService;

	@Autowired
	private ChecimalTreatmentService chemicalTreatmentService;

	@Lazy
	@Autowired
	private SummaryService summaryService;

	@Autowired
	private DailyBargeCrudeUploadDownloadService dailyBargeCrudeUploadDownloadService;

	@Autowired
	private HidrocarbonsService hidrocarbonsService;

	@Autowired
	private HourlyReinjectionService hourlyReinjectionService;

	@Autowired
	private HourlyEspService hourlyEspService;

	@Autowired
	public DailyChemicalSummaryService dailyChemicalSummaryService;

	@Autowired
	public DispatchTankReportedLevelsService dispatchTankReportedLevelsService;

	@Autowired
	public DailyWellServiceService dailyWellService;

	@Autowired
	public ConsolidatedDownTimeService consolidatedDownTimeService;

	@Autowired
	public FeignClientForDeviceManagementSystem feignClientForDeviceManagementSystem;
	@Autowired
	public RealTimeSolidsMonitoringService realTimeSolidsMonitoringService;

	@Autowired
	public EspDiagnosticsDataService espDiagnosticsDataService;

	@Autowired
	public Genericservices genericservices;
	
	@Override
	public CustomResponse uploadFile(MultipartFile multipartFile, String category, Date operationDateTime,
			CustomUserDetails customUserDetails) {
		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();

		GlobalFileUploads fileUploads = null;
		CustomResponse customResponse = null;
		File destinationFile = null;
		Double index = 0.0;
		try {

			File destinationFolder = new File(environment.getProperty("spring.history-uploads") + File.separator
					+ category + File.separator + StringUtil.convertDateIntoYYMMdd(operationDateTime));

			if (!destinationFolder.exists()) {
				destinationFolder.mkdirs();
			}
			destinationFile = new File(destinationFolder.getPath(), multipartFile.getOriginalFilename());
			if (destinationFile.exists()) {
				destinationFile.delete();
				/*
				 * FileInputStream input = new FileInputStream(destinationFile); MultipartFile
				 * multipartFile1 = new MockMultipartFile("file", destinationFile.getName(),
				 * "text/plain", IOUtils.toByteArray(input));
				 */

				/*
				 * return new CustomResponse(Boolean.FALSE,
				 * CustomExceptionCode.FILE_ALREADY_EXIST.getErrCode(),
				 * CustomExceptionCode.FILE_ALREADY_EXIST.getErrMsg(),
				 * CustomExceptionCode.FILE_ALREADY_EXIST.getErrMsg(), metaInfo);
				 */
			}
			switch (category) {
			case "IMPORT 1. BES Daily Data":
				index = 1.0;
				String dates = "2023-08-01 00:00:00.0";
				Date dates1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dates);
				if (operationDateTime.before(dates1)) {
					customResponse = espService.importHourlyEspFromExcel(customUserDetails, multipartFile,
							operationDateTime);
				}
				 else {
						customResponse = dailyEspService.importDailyEspFromExcel(customUserDetails, multipartFile,
								operationDateTime);
				}

				
				break;
			case "IMPORT 2. UBH-MTU Hourly Data":
				index = 2.0;
				customResponse = hourlyHpUBHService.importHourlyHpUBHServiceFromExcel(customUserDetails, multipartFile,
						operationDateTime);
				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyHpUBH>) customResponse.getData(), HourlyHpUBH.class);
				}

				break;
			case "IMPORT 3. HPS Sertecpet Hourly Data":
				index = 3.0;
				customResponse = hourlyHPSServiceImpl.importHourlyHps(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 4. REINJECTION WELL HPS Hourly Data":
				index = 4.0;
				customResponse = hourlyReinjectionService.importReinjection(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 5. DOWNTIME":
				index = 5.0;
				customResponse = dailyDeferredProductionService.importDowntimeSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 6. PRUEBA DE POZOS HORA A HORA":
				index = 6.0;
				customResponse = hourlyWellTestServiceImpl.importHourlywelltest(customUserDetails, multipartFile,
						operationDateTime);

				break;
			case "IMPORT 7. CONTROL DE QUIMICA":
				index = 7.0;
				customResponse = dailyReinjectionService.importDailyReinjectionFromExcel(customUserDetails,
						multipartFile, operationDateTime);
				break;
			case "IMPORT 8. CONTROL DE CALIDAD":
				index = 8.0;
				String dat = "2023-03-09 00:00:00.0";
				Date dat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dat);
				if (operationDateTime.before(dat1)) {
					customResponse = hourlyLaboratoryAnalysisASTMService.importHourlyLaboratoryAnalysisASTMFromExcel(
							customUserDetails, multipartFile, operationDateTime);
				} else {
					customResponse = hourlyLaboratoryAnalysisASTMService.importHourlyLaboratoryAnalysisASTMFromExcelNew(
							customUserDetails, multipartFile, operationDateTime);
				}

				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyLaboratoryAnalysisASTM>) customResponse.getData(),
							HourlyLaboratoryAnalysisASTM.class);
				}
				break;
			case "IMPORT 8-1. CALIDAD WATERCUT":
				index = 8.1;
				customResponse = hourlyLaboratoryWatercutSerivce
						.importHourlyLaboratoryWaterCutFromExcel(customUserDetails, multipartFile, operationDateTime);
				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyLaboratoryWatercut>) customResponse.getData(),
							HourlyLaboratoryWatercut.class);
				}
				break;
			case "IMPORT 9. DAILY TANK MEASUREMENTS":
				index = 9.0;
				customResponse = dailyDiluentTanklevelAndFiscalizationService
						.importDailyDiluentTanklevelAndFiscalizationSheet(customUserDetails, multipartFile,
								operationDateTime);
				if (!customResponse.isSuccess()) {
					dailyDiluentTanklevelAndFiscalizationService
							.deleteDailyDiluentTanklevelAndFiscalizationByOperationDateService(customUserDetails,
									operationDateTime);
				}

				break;
			case "IMPORT 9-1. DAILY TANK MEASUREMENTS":
				index = 9.1;
				customResponse = dispatchTankReportedLevelsService
						.importDispatchTankReportedLevelsSheet(customUserDetails, multipartFile, operationDateTime);
				if (!customResponse.isSuccess()) {
					dispatchTankReportedLevelsService.deleteDispatchTankReportedLevelsByOperationDateService(
							customUserDetails, operationDateTime);
				}
				break;
			case "IMPORT 10. RESUMEN DIARIO DE PRODUCCION":
				index = 10.0;
				String oldstring = "2022-04-05 00:00:00.0";
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);
				if (operationDateTime.before(date)) {
					customResponse = this.import10FromExcel(customUserDetails, multipartFile, operationDateTime,
							metaInfo);
				} else {
					customResponse = this.import10FromExcelNew(customUserDetails, multipartFile, operationDateTime,
							metaInfo);
				}
				break;
			case "IMPORT 11. REPORTE DE CARGA Y DESCARGA":
				index = 11.0;
				customResponse = hidrocarbonsService.importHidrocarbonsServiceSheet(customUserDetails, multipartFile,
						operationDateTime);
				customResponse = dailyBargeCrudeUploadDownloadService
						.importDailyBargeCrudeUploadDownloadSheet(customUserDetails, multipartFile, operationDateTime);

				if (!customResponse.isSuccess()) {
					dailyBargeCrudeUploadDownloadService.deleteDailyBargeCrudeUploadDownloadByOperationDateService(
							customUserDetails, operationDateTime);
					Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
					Date endDate = StringUtil.setDateEndOfTheDayNew(operationDateTime);
					Query query = new Query();
					query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("end_date")
							.gte(startDate).lt(endDate));
					mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
					mongoOperations.remove(query, "db_heavy_fuel_oil");
					mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");
				}

//				customResponse = wellTestDataServiceImpl.importWellTestExcel(customUserDetails, multipartFile,
//						wellName);
				break;
			case "IMPORT 12. PLANT EVENTS":
				index = 12.0;
				customResponse = dailyPlantTaskAndTimeService.importPlantEventSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 13. Daily Well Service":
				index = 13.0;
				customResponse = dailyWellService.importDailyWellServiceSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;

			case "PRODUCTION BLOCK 95":
				customResponse = completionFileservice.inputForDbImports(customUserDetails, multipartFile);
				break;
			default:
				customResponse = new CustomResponse(Boolean.TRUE,
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
						"category miss match", metaInfo);
			}
			if (customResponse.isSuccess()) {
				if (!multipartFile.isEmpty()) {
					multipartFile.transferTo(destinationFile);

					if (destinationFile.exists()) {

						Path path = Paths.get(destinationFile.getPath());
						Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
					}
					fileUploads = new GlobalFileUploads();
					fileUploads.setFileName(multipartFile.getOriginalFilename());
					fileUploads.setCategory(category);
					fileUploads.setOperationDateTime(operationDateTime);
					fileUploads.setIndex(index);
					fileUploads.setFilePath(destinationFile.getAbsolutePath()
							.split("ROOT")[destinationFile.getAbsolutePath().split("ROOT").length - 1]);
					mongoTemplate.save(fileUploads);
				}

			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			return new CustomResponse(Boolean.FALSE,
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
					metaInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return new CustomResponse(Boolean.FALSE,
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
					metaInfo);
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		return customResponse;
	}

	private CustomResponse import10FromExcel(CustomUserDetails customUserDetails, MultipartFile multipartFile,
			Date operationDateTime, HashMap<String, String> metaInfo) {
		// Date matching is operated in DailyRestTankResponse.
		CustomResponse dailyWellParametersResponse = dailyWellParametersService
				.importdailyWellParametersServiceFromExcel(customUserDetails, multipartFile, operationDateTime);

		if (!dailyWellParametersResponse.isSuccess())
			return dailyWellParametersResponse;

		CustomResponse dailyRestTankResponse = dailyRestTanksService.importDailyRestTankExcel(customUserDetails,
				multipartFile, operationDateTime);
		if (!dailyRestTankResponse.isSuccess())
			return dailyRestTankResponse;

		CustomResponse dailyTankAvailableResponse = dailyTankAvailableStorageService
				.importDailyTankAvailableStorageExcel(customUserDetails, multipartFile, operationDateTime);
		if (!dailyTankAvailableResponse.isSuccess())
			return dailyTankAvailableResponse;

		CustomResponse dailyWaterTankResponse = dailyWaterTankService.importDailyWaterTankExcel(customUserDetails,
				multipartFile, operationDateTime);
		if (!dailyWaterTankResponse.isSuccess())
			return dailyWaterTankResponse;

		CustomResponse dailyChemicalSummaryResponse = dailyChemicalSummaryService
				.importDailyChemicalSummaryFromExcel(customUserDetails, multipartFile, operationDateTime);
		if (!dailyChemicalSummaryResponse.isSuccess())
			return dailyChemicalSummaryResponse;

		CustomResponse dailyWaterBalanceResponse = dailyWaterBalanceService
				.importDailyWaterBalanceServiceExcel(customUserDetails, multipartFile, operationDateTime);
		if (!dailyWaterBalanceResponse.isSuccess())
			return dailyWaterBalanceResponse;

//		CustomResponse dailyReinjectionResponse = dailyReinjectionService
//				.importDailyReinjectionFromExcel(customUserDetails, multipartFile, operationDateTime);

		if (dailyChemicalSummaryResponse.isSuccess() && dailyWaterBalanceResponse.isSuccess()
				&& dailyWaterTankResponse.isSuccess() && dailyRestTankResponse.isSuccess()
				&& dailyTankAvailableResponse.isSuccess() && dailyWellParametersResponse.isSuccess()) {
			List<Object> list = new ArrayList<Object>();
			list.addAll((List<DailyChemicalSummary>) dailyChemicalSummaryResponse.getData());
			list.addAll((List<DailyWaterBalance>) dailyWaterBalanceResponse.getData());
			list.addAll((List<DailyWaterTank>) dailyWaterTankResponse.getData());
			list.addAll((List<DailyRestTanks>) dailyRestTankResponse.getData());
			list.addAll((List<DailyTankAvailableStorage>) dailyTankAvailableResponse.getData());
			list.addAll((List<DailyWellParameters>) dailyWellParametersResponse.getData());
			mongoOperations.insertAll(list);

			return new CustomResponse(SuccessCode.IMPORT_10_SAVED_SUCCESS.getSuccessCode(),
					SuccessCode.IMPORT_10_SAVED_SUCCESS.getSuccessMessage(), "Import Done Successfully", metaInfo);
		} else {
			return new CustomResponse(Boolean.FALSE, CustomExceptionCode.IMPORT_10_SAVE_EXCEPTION.getErrCode(),
					CustomExceptionCode.IMPORT_10_SAVE_EXCEPTION.getErrMsg(), "Importing Failed", metaInfo);
		}
	}

	private CustomResponse import10FromExcelNew(CustomUserDetails customUserDetails, MultipartFile multipartFile,
			Date operationDateTime, HashMap<String, String> metaInfo) {

		CustomResponse dailyWellParametersResponse = dailyWellParametersService
				.importdailyWellParametersServiceFromExcelNew(customUserDetails, multipartFile, operationDateTime);

		if (!dailyWellParametersResponse.isSuccess())
			return dailyWellParametersResponse;

		CustomResponse dailyWaterBalanceResponse = dailyWaterBalanceService
				.importDailyWaterBalanceServiceExcelNew(customUserDetails, multipartFile, operationDateTime);
		if (!dailyWaterBalanceResponse.isSuccess())
			return dailyWaterBalanceResponse;

		CustomResponse dailyRestTankResponse = dailyRestTanksService.importDailyRestTankExcelNew(customUserDetails,
				multipartFile, operationDateTime);
		if (!dailyRestTankResponse.isSuccess())
			return dailyRestTankResponse;

		CustomResponse dailyTankAvailableResponse = dailyTankAvailableStorageService
				.importDailyTankAvailableStorageExcelNew(customUserDetails, multipartFile, operationDateTime);
		if (!dailyTankAvailableResponse.isSuccess())
			return dailyTankAvailableResponse;

		CustomResponse dailyChemicalSummaryResponse = dailyChemicalSummaryService
				.importDailyChemicalSummaryFromExcelNew(customUserDetails, multipartFile, operationDateTime);
		if (!dailyChemicalSummaryResponse.isSuccess())
			return dailyChemicalSummaryResponse;

		CustomResponse dailyWaterTankResponse = dailyWaterTankService.importDailyWaterTankExcelNew(customUserDetails,
				multipartFile, operationDateTime);
		if (!dailyWaterTankResponse.isSuccess())
			return dailyWaterTankResponse;

//		CustomResponse dailyReinjectionResponse = dailyReinjectionService
//				.importDailyReinjectionFromExcel(customUserDetails, multipartFile, operationDateTime);

		if (dailyChemicalSummaryResponse.isSuccess() && dailyWaterBalanceResponse.isSuccess()
				&& dailyWaterTankResponse.isSuccess() && dailyRestTankResponse.isSuccess()
				&& dailyTankAvailableResponse.isSuccess() && dailyWellParametersResponse.isSuccess()) {
			List<Object> list = new ArrayList<Object>();
			list.addAll((List<DailyChemicalSummary>) dailyChemicalSummaryResponse.getData());
			list.addAll((List<DailyWaterBalance>) dailyWaterBalanceResponse.getData());
			list.addAll((List<DailyWaterTank>) dailyWaterTankResponse.getData());
			list.addAll((List<DailyRestTanks>) dailyRestTankResponse.getData());
			list.addAll((List<DailyTankAvailableStorage>) dailyTankAvailableResponse.getData());
			list.addAll((List<DailyWellParameters>) dailyWellParametersResponse.getData());
			mongoOperations.insertAll(list);

			return new CustomResponse(SuccessCode.IMPORT_10_SAVED_SUCCESS.getSuccessCode(),
					SuccessCode.IMPORT_10_SAVED_SUCCESS.getSuccessMessage(), "Import Done Successfully", metaInfo);
		} else {
			return new CustomResponse(Boolean.FALSE, CustomExceptionCode.IMPORT_10_SAVE_EXCEPTION.getErrCode(),
					CustomExceptionCode.IMPORT_10_SAVE_EXCEPTION.getErrMsg(), "Importing Failed", metaInfo);
		}

	}

	@Override
	public Page<GlobalFileUploads> getUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
		String methodName = "getAllRemarks";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Page<GlobalFileUploads> globalFileUploads = null;
		try {
			if (StringUtil.haveValue(dateRangeVM.getCategoryList())) {
				if (StringUtil.haveValue(dateRangeVM.getStartDate())
						&& StringUtil.haveValue(dateRangeVM.getEndDate())) {
					globalFileUploads = globalFileUploadsRepository
							.findByCategoryInAndDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(
									dateRangeVM.getCategoryList(), false,
									StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
									StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
				} else {
					globalFileUploads = globalFileUploadsRepository.findByCategoryInAndDeleteFlagOrderByIndexAsc(
							dateRangeVM.getCategoryList(), false, pageable);
				}
			} else if (StringUtil.haveValue(dateRangeVM.getCategory())) {
				if (dateRangeVM.getCategory().startsWith("TEMPLETE")) {
					if (StringUtil.haveValue(dateRangeVM.getStartDate())
							&& StringUtil.haveValue(dateRangeVM.getEndDate())) {
						globalFileUploads = globalFileUploadsRepository
								.findByCategoryAndDeleteFlagAndOperationDateTimeBetweenOrderByModifiedDateTimeDesc(
										dateRangeVM.getCategory(), false,
										StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
										StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
					} else {
						globalFileUploads = globalFileUploadsRepository
								.findByCategoryAndDeleteFlagOrderByModifiedDateTimeDesc(dateRangeVM.getCategory(),
										false, pageable);
					}
				} else {
					if (StringUtil.haveValue(dateRangeVM.getStartDate())
							&& StringUtil.haveValue(dateRangeVM.getEndDate())) {
						globalFileUploads = globalFileUploadsRepository
								.findByCategoryAndDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(
										dateRangeVM.getCategory(), false,
										StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
										StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
					} else {
						globalFileUploads = globalFileUploadsRepository
								.findByCategoryAndDeleteFlagOrderByIndexAsc(dateRangeVM.getCategory(), false, pageable);
					}
				}
			} else if (StringUtil.haveValue(dateRangeVM.getStartDate())
					&& StringUtil.haveValue(dateRangeVM.getEndDate())) {
				globalFileUploads = globalFileUploadsRepository
						.findByDeleteFlagAndOperationDateTimeBetweenOrderByIndexAsc(false,
								StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
								StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
			} else {
				globalFileUploads = globalFileUploadsRepository
						.findByDeleteFlagOrderByIndexAsc(dateRangeVM.getCategory(), false, pageable);
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return globalFileUploads;
	}

	@Override
	public Map<String, Long> countDownloadedFiles(CustomUserDetails customUserDetails,
			FilesDownloadedCountDomain filesDownloadedCount) {
		String methodName = "countDownloadedFiles";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, Long> count = new HashMap<>();
		try {
			if (StringUtil.haveValue(filesDownloadedCount.getFileName())) {
				filesDownloadedCount.setUserName(customUserDetails.getUsername());
				filesDownloadedCount.setFileDownloadedDate(new Date());
				mongoTemplate.save(filesDownloadedCount);
			}
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.USER_NAME).is(customUserDetails.getUsername())
					.and(Constants.DELETE_FLAG).is(false));
			count.put("total", mongoTemplate.count(query, FilesDownloadedCountDomain.class));
			query.addCriteria(Criteria.where("file_downloaded_date")
					.gte(StringUtil.setDateBegniningOfTheDay(new Date())).lte(new Date()));
			count.put("today", mongoTemplate.count(query, FilesDownloadedCountDomain.class));
			return count;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.FTP_FILES_COUNT_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCORED_WHILE_SAVING_DOWNLOADED_COUNT.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCORED_WHILE_SAVING_DOWNLOADED_COUNT.getErrCode());
		}
	}

	@Override
	public Map<String, Long> numericalAnalysisForFtp(CustomUserDetails customUserDetails) {
		String methodName = "getNumericalAnalysis";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Map<String, Long> map = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.CREATED_BY).is(customUserDetails.getUsername())
					.and(Constants.DELETE_FLAG).is(false));
			List<GlobalFileUploads> allData = mongoTemplate.find(query, GlobalFileUploads.class);
			Date startDate = StringUtil.setDateBegniningOfTheDay(new Date());
			if (StringUtil.haveElements(allData)) {
				map = new HashMap<>();
				long total = Long.valueOf(allData.size());
				map.put("todayReceived", allData.stream()
						.filter(x -> x.getCreatedOn().after(startDate) && x.getCreatedOn().before(new Date())).count());
				map.put("todayProcessed", map.get("todayReceived"));
				map.put("received", total);
				map.put("processed", total);
			}
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.FTP_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCORED_WHILE_FETCHING_FTP_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCORED_WHILE_FETCHING_FTP_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return map;
	}

	@Override
	public GlobalFileUploads updateApprovedStatus(String globalFileUploadsId, CustomUserDetails customUserDetails) {
		String methodName = "updateApprovedStatus";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		GlobalFileUploads globalFileUpload = new GlobalFileUploads();
		try {

			globalFileUpload = globalFileUploadsRepository.findByGlobalFileUploadsIdAndDeleteFlag(globalFileUploadsId,
					false);

			if (StringUtil.haveValue(globalFileUpload)) {
				globalFileUpload.setApproved(true);
				globalFileUpload = globalFileUploadsRepository.save(globalFileUpload);
			} else {
				throw new CustomParameterizedException(
						RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return globalFileUpload;
	}

	@Override
	public Object chronProcessed(CustomUserDetails customUserDetails) {
		String methodName = "updateApprovedStatus";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
			// chron 1
			dailyEspService.addDailyEsp(customUserDetails, new DailyEsp());

			// chron 2
			dailyHpUBHService.addDailyHpUBH(customUserDetails, new DailyHpUBH());

			// chron 3

			dailyHPSService.addDaillyHPS(customUserDetails, new DailyHPS());

			// chron 4

			dailyWellTestService.addDailyWellTest(customUserDetails, new DailyWellTest());

			// chron 5

			dailyWellParametersService.addDailyWellParameters(customUserDetails, new DailyWellParameters());

			// chron 6

			dailyWellPotentialService.addDailyWellPotentialProduction(customUserDetails, new DailyWellPotential());

			// chron 7
			dailyTotalWellPotentialAndProductionService.saveDailyTotalWellPAP(customUserDetails,
					new DailyTotalWellPotentialAndProduction());

			// chron 8
			dailyTotalmixtanklevelAndFiscalizationService.addDailyTotalmixtanklevelAndFiscalization(customUserDetails,
					new DailyTotalmixtanklevelAndFiscalization());

			// chron 9
			dispatchTanksService.addDispatchTanks(customUserDetails, new DispatchTanks());

			/*
			 * Doubt need to ask ramana chron 10
			 * dailyTankAvailableStorageService.addDailyTankAvailableStorage(
			 * customUserDetails, new DailyTankAvailableStorage());
			 */

			// chron 11
			dailyTotalTankAvailableStorageService.addDailyTotalTankAvailableStorage(customUserDetails,
					new DailyTotalTankAvailableStorage());

			// chron 12
			dailyTotalDiluentTankLevelAndFiscalizationService.addDailyTotalDiluentTankLevelAndFiscalization(
					customUserDetails, new DailyTotalDiluentTankLevelAndFiscalization());

			// chron 13
			diluentTanksService.addDiluentTanks(customUserDetails, new DiluentTanks());

			// chron 14
			dailyTankTotalService.addDailyTankTotal(customUserDetails, new DailyTankTotals());

			// chron 15
			dailyCrudeShipmentService.addDailyCrudeShipment(customUserDetails, new DailyCrudeShipment());

			// chron 16
			dailyOilForGenerationService.addDailyOilForGeneration(customUserDetails, new DailyOilForGeneration());

			// chron 17
			dtCrudeShipmentAndGenerationService.addDtCrudeShipmentAndGeneration(customUserDetails,
					new DtCrudeShipmentAndGeneration());

			// chron 18
			dailyOilBalanceService.addDailyOilBalance(customUserDetails, new DailyOilBalance());

			// chron 19
			productionService.saveProduction(new Production(), customUserDetails);

			// chron 20
			productionService.saveMonthlyProduction(new MonthlyProduction(), customUserDetails);

			// chron 21
			reinjectionService.saveReinjection(new Reinjection(), customUserDetails);

			// chron 22
			oilLoadingandFiscalizationService.saveFiscalizationData(new OilLoadingandFiscalization(),
					customUserDetails);

			// chron 23
			dbDiluentUnloadingAndFiscalizaService.addDbDiluentUnloadingAndFiscaliza(customUserDetails,
					new DbDiluentUnloadingAndFiscaliza());

			// chron 24
			dbHeavyFuelOilService.addDbHeavyFuelOil(customUserDetails, new DbHeavyFuelOil());

			// chron 25
			dbOilBargesService.addDbOilBarges(customUserDetails, new DbOilBarges());

			// chron 26
			dbDilutentBargesService.addDbDilutentBarges(customUserDetails, new DbDilutentBarges());

			// chron 27
			laboratoryService.addLaboratory(customUserDetails, new DbLaboratory());

			// chron 28
			diluentService.addDaillyHPS(customUserDetails, new DbDiluent());

			// chron 29
			consolidatedDiluentService.addDaillyHPS(customUserDetails, new ConsolidatedDiluent());

			// chron 30
			downtimeService.addDbDownTime(customUserDetails, new DbDownTime());

			// chron 31
			chemicalTreatmentService.addDaillyHPS(customUserDetails, new ChemicalTreatment());
			// chron 32
			summaryService.saveSummaryData(new Summary(), customUserDetails);

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return true;
	}

	@Override
	public Object chronProcessedWithDate(CustomUserDetails customUserDetails, Date operationDateTime) {
		String methodName = "chronProcessedWithDate";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {
			Date processedDate = getLatestDate(customUserDetails);

			if (operationDateTime.compareTo(processedDate) != 0) {
				throw new CustomParameterizedException(
						RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
						CustomExceptionCode.CHRON_PROCESS_EXCEPTION.getErrMsg(),
						CustomExceptionCode.CHRON_PROCESS_EXCEPTION.getErrCode()
								+ StringUtil.convertDateIntoYYMMdd(processedDate));
			}

			Date latestApprovedDate = getLatestApprovedDate(customUserDetails);
			SearchVM searchVM = new SearchVM();
			searchVM.setStartDate(StringUtil.convertDateIntoYYMMdd(operationDateTime));
			searchVM.setEndDate(StringUtil.convertDateIntoYYMMdd(latestApprovedDate));

			return chronProcessedWithDateSupport(searchVM, customUserDetails);

			/*
			 * // chron 1 DailyEsp esp = new DailyEsp();
			 * esp.setOperationDateTime(operationDateTime);
			 * dailyEspService.addDailyEsp(customUserDetails, esp);
			 * 
			 * // chron 2 DailyHpUBH dailyHpUBH = new DailyHpUBH();
			 * dailyHpUBH.setOperationDateTime(operationDateTime);
			 * dailyHpUBHService.addDailyHpUBH(customUserDetails, dailyHpUBH);
			 * 
			 * // chron 3 DailyHPS dailyHPS = new DailyHPS();
			 * dailyHPS.setOperationDateTime(operationDateTime);
			 * dailyHPSService.addDaillyHPS(customUserDetails, dailyHPS);
			 * 
			 * // chron 4 DailyReinjection dailyReinjection = new DailyReinjection();
			 * dailyReinjection.setOperationDateTime(operationDateTime);
			 * dailyReinjectionService.addDailyReinjection(customUserDetails,
			 * dailyReinjection);
			 * 
			 * // chron 5
			 * 
			 * DailyWellTest
			 * 
			 * 
			 * // chron 6 DailyWellParameters dailyWellParameters = new
			 * DailyWellParameters(); dailyWellParameters.setDateTime(operationDateTime);
			 * dailyWellParametersService.addDailyWellParameters(customUserDetails,
			 * dailyWellParameters);
			 * 
			 * // chron 7 DailyWellPotential dailyWellPotential = new DailyWellPotential();
			 * dailyWellPotential.setOperationDateTime(operationDateTime);
			 * dailyWellPotentialService.addDailyWellPotentialProduction(customUserDetails,
			 * dailyWellPotential);
			 * 
			 * // chron 8 DailyTotalWellPotentialAndProduction
			 * dailyTotalWellPotentialAndProduction = new
			 * DailyTotalWellPotentialAndProduction();
			 * dailyTotalWellPotentialAndProduction.setOperationDateTime(operationDateTime);
			 * dailyTotalWellPotentialAndProductionService.saveDailyTotalWellPAP(
			 * customUserDetails, dailyTotalWellPotentialAndProduction);
			 * 
			 * // chron 9 DailyTotalmixtanklevelAndFiscalization
			 * dailyTotalmixtanklevelAndFiscalization = new
			 * DailyTotalmixtanklevelAndFiscalization();
			 * dailyTotalmixtanklevelAndFiscalization.setOperationDateTime(operationDateTime
			 * ); dailyTotalmixtanklevelAndFiscalizationService.
			 * addDailyTotalmixtanklevelAndFiscalization(customUserDetails,
			 * dailyTotalmixtanklevelAndFiscalization);
			 * 
			 * // chron 10 DispatchTanks dispatchTanks = new DispatchTanks();
			 * dispatchTanks.setOperationDateTime(operationDateTime);
			 * dispatchTanksService.addDispatchTanks(customUserDetails, dispatchTanks);
			 * 
			 * // chron 10-1 DispatchTanksReported dispatchTanksReported = new
			 * DispatchTanksReported();
			 * dispatchTanksReported.setOperationDateTime(operationDateTime);
			 * dispatchTanksReportedService.addDispatchTanksReported(customUserDetails,
			 * dispatchTanksReported);
			 * 
			 * // chron 11 DailyTotalTankAvailableStorage dailyTotalTankAvailableStorage =
			 * new DailyTotalTankAvailableStorage();
			 * dailyTotalTankAvailableStorage.setOperationDateTime(operationDateTime);
			 * dailyTotalTankAvailableStorageService.addDailyTotalTankAvailableStorage(
			 * customUserDetails, dailyTotalTankAvailableStorage);
			 * 
			 * // chron 12 DailyTotalDiluentTankLevelAndFiscalization
			 * dailyTotalDiluentTankLevelAndFiscalization = new
			 * DailyTotalDiluentTankLevelAndFiscalization();
			 * dailyTotalDiluentTankLevelAndFiscalization.setOperationDateTime(
			 * operationDateTime); dailyTotalDiluentTankLevelAndFiscalizationService.
			 * addDailyTotalDiluentTankLevelAndFiscalization( customUserDetails,
			 * dailyTotalDiluentTankLevelAndFiscalization);
			 * 
			 * // chron 13 DiluentTanks diluentTanks = new DiluentTanks();
			 * diluentTanks.setOperationDateTime(operationDateTime);
			 * diluentTanksService.addDiluentTanks(customUserDetails, diluentTanks);
			 * 
			 * // chron 14 DailyTankTotals dailyTankTotals = new DailyTankTotals();
			 * dailyTankTotals.setOperationDateTime(operationDateTime);
			 * dailyTankTotalService.addDailyTankTotal(customUserDetails, dailyTankTotals);
			 * 
			 * // chron 15 doubt EMBARQUE is for entire table or for operation number
			 * DailyCrudeShipment dailyCrudeShipment = new DailyCrudeShipment();
			 * dailyCrudeShipment.setOperationDateTime(operationDateTime);
			 * dailyCrudeShipmentService.addDailyCrudeShipment(customUserDetails,
			 * dailyCrudeShipment);
			 * 
			 * // chron 16 DailyOilForGeneration dailyOilForGeneration = new
			 * DailyOilForGeneration();
			 * dailyOilForGeneration.setOperationDateTime(operationDateTime);
			 * dailyOilForGenerationService.addDailyOilForGeneration(customUserDetails,
			 * dailyOilForGeneration);
			 * 
			 * // chron 17 DtCrudeShipmentAndGeneration dtCrudeShipmentAndGeneration = new
			 * DtCrudeShipmentAndGeneration();
			 * dtCrudeShipmentAndGeneration.setOperationDateTime(operationDateTime);
			 * dtCrudeShipmentAndGenerationService.addDtCrudeShipmentAndGeneration(
			 * customUserDetails, dtCrudeShipmentAndGeneration);
			 * 
			 * // chron 18 need to know Stock Skimmed Oil and Real Stock Skimmed Oil
			 * DailyOilBalance dailyOilBalance = new DailyOilBalance();
			 * dailyOilBalance.setOperationDateTime(operationDateTime);
			 * dailyOilBalanceService.addDailyOilBalance(customUserDetails,
			 * dailyOilBalance);
			 * 
			 * // newly added ConsolidatedDownTime
			 * 
			 * ConsolidatedDownTime consolidatedDownTime = new ConsolidatedDownTime();
			 * consolidatedDownTime.setOperationDateTime(operationDateTime);
			 * consolidatedDownTimeService.addConsolidatedDownTime(customUserDetails,
			 * consolidatedDownTime);
			 * 
			 * // chron 19 Production production = new Production();
			 * production.setProductionDate(operationDateTime); List<Production>
			 * productionList = productionService.saveProduction(production,
			 * customUserDetails);
			 * 
			 * // chron 20 MonthlyProduction monthlyProduction = new MonthlyProduction();
			 * monthlyProduction.setProductionDate(operationDateTime);
			 * productionService.saveMonthlyProduction(monthlyProduction,
			 * customUserDetails);
			 * 
			 * // chron 21 Reinjection reinjection = new Reinjection();
			 * reinjection.setReinjectionDate(operationDateTime);
			 * reinjectionService.saveReinjection(reinjection, customUserDetails);
			 * 
			 * // chron 22 OilLoadingandFiscalization oilLoadingandFiscalization = new
			 * OilLoadingandFiscalization();
			 * oilLoadingandFiscalization.setFiscalizationDate(operationDateTime);
			 * oilLoadingandFiscalizationService.saveFiscalizationData(
			 * oilLoadingandFiscalization, customUserDetails);
			 * 
			 * // chron 23 DbDiluentUnloadingAndFiscaliza dbDiluentUnloadingAndFiscaliza =
			 * new DbDiluentUnloadingAndFiscaliza();
			 * dbDiluentUnloadingAndFiscaliza.setDateTime(operationDateTime);
			 * dbDiluentUnloadingAndFiscalizaService.addDbDiluentUnloadingAndFiscaliza(
			 * customUserDetails, dbDiluentUnloadingAndFiscaliza);
			 * 
			 * // chron 24 DbHeavyFuelOil dbHeavyFuelOil = new DbHeavyFuelOil();
			 * dbHeavyFuelOil.setDateTime(operationDateTime);
			 * dbHeavyFuelOilService.addDbHeavyFuelOil(customUserDetails, dbHeavyFuelOil);
			 * 
			 * // chron 25 DbOilBarges dbOilBarges = new DbOilBarges();
			 * dbOilBarges.setIntialDate(operationDateTime);
			 * dbOilBargesService.addDbOilBarges(customUserDetails, dbOilBarges);
			 * 
			 * // chron 26 DbDilutentBarges dbDilutentBarges = new DbDilutentBarges();
			 * dbDilutentBarges.setIntialDate(operationDateTime);
			 * dbDilutentBargesService.addDbDilutentBarges(customUserDetails,
			 * dbDilutentBarges);
			 * 
			 * // chron 27 DbLaboratory dbLaboratory = new DbLaboratory();
			 * dbLaboratory.setDate(operationDateTime);
			 * laboratoryService.addLaboratory(customUserDetails, dbLaboratory);
			 * 
			 * // chron 28 DbDiluent dbDiluent = new DbDiluent();
			 * dbDiluent.setFecha(operationDateTime);
			 * diluentService.addDaillyHPS(customUserDetails, dbDiluent);
			 * 
			 * // chron 29 ConsolidatedDiluent consolidatedDiluent = new
			 * ConsolidatedDiluent(); consolidatedDiluent.setFecha(operationDateTime);
			 * consolidatedDiluentService.addDaillyHPS(customUserDetails,
			 * consolidatedDiluent);
			 * 
			 * // chron 30 DbDownTime dbDownTime = new DbDownTime();
			 * dbDownTime.setDate(operationDateTime);
			 * downtimeService.addDbDownTime(customUserDetails, dbDownTime);
			 * 
			 * // chron 31 00 petrotal file all are user entered ChemicalTreatment
			 * 
			 * // chron 32 Summary summary = new Summary();
			 * summary.setSummaryDate(operationDateTime);
			 * summaryService.saveSummaryData(summary, customUserDetails);
			 * 
			 * List<GlobalFileUploads> gobalList =
			 * globalFileUploadsRepository.findByDeleteFlagAndOperationDateTimeBetween(
			 * false, StringUtil.setDateBegniningOfTheDayNew(operationDateTime),
			 * StringUtil.setDateEndOfTheDayNew(operationDateTime));
			 * 
			 * for (GlobalFileUploads files : gobalList) { files.setApproved(true);
			 * files.setProcessed(true); } globalFileUploadsRepository.save(gobalList);
			 * 
			 * ExtraTableChron extraTableChron = new ExtraTableChron(); Query query = new
			 * Query(); query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			 * extraTableChron = mongoOperations.findOne(query, ExtraTableChron.class); if
			 * (StringUtil.haveValue(extraTableChron)) { if
			 * (!extraTableChron.getDates().contains(operationDateTime))
			 * extraTableChron.getDates().add(operationDateTime); } else { extraTableChron =
			 * new ExtraTableChron(); List<Date> date = new ArrayList<Date>();
			 * date.add(operationDateTime); extraTableChron.setDates(date); }
			 * mongoOperations.save(extraTableChron);
			 * 
			 * String content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n"
			 * + "<body>\n" + "<div style=\"width: 90%; margin: auto\">\n" + "	\n" +
			 * "	<div class=\"graphView\">\n" +
			 * "		<h4> Daily document uploads successfully done for the following Date "
			 * + StringUtil.convertDateIntoYYMMdd(operationDateTime) + " by " +
			 * customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n" +
			 * "</body>\n" + "</html>"; String subject = "Daily Document Upload";
			 * 
			 * reportMailSend(customUserDetails, content, subject);
			 */

		} catch (CustomParameterizedException e) {
			onChronProcessFailedRemoveData(operationDateTime, customUserDetails);
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			onChronProcessFailedRemoveData(operationDateTime, customUserDetails);
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
//		log.info(this.getClass() + " " + methodName + " " + Constants.END);
//		return true;
	}

	@Override
	public Object getImportFileNames(CustomUserDetails customUserDetails) {
		String methodName = "getImportFileNames";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		List<String> importNames;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			List<ImportXlSheets> allData = mongoTemplate.find(query, ImportXlSheets.class);
			importNames = allData.stream().map(e -> e.getFileName()).distinct().collect(Collectors.toList());
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return importNames;
	}

	@Override
	public Object rejectByFileUploadId(String globalFileUploadsId, CustomUserDetails customUserDetails) {
		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();
		GlobalFileUploads fileUploads = new GlobalFileUploads();
		Object obj = null;
		try {
			fileUploads = globalFileUploadsRepository.findByGlobalFileUploadsIdAndDeleteFlag(globalFileUploadsId,
					false);
			if (fileUploads.isApproved() || fileUploads.isProcessed()) {
				throw new CustomParameterizedException(
						RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(),
						"Approved or Processed Files Cannot Be Rejected");
			}
			if (StringUtil.haveValue(fileUploads)) {
				File destinationFile = new File(environment.getProperty("spring.history-uploads").split("ROOT")[0]
						+ "ROOT" + File.separator + fileUploads.getFilePath());
				if (destinationFile.exists()) {
					destinationFile.delete();
				}
				switch (fileUploads.getCategory()) {
				case "IMPORT 1. BES Hourly Data":
					obj = espService.deleteHourlyESPByOperationDateService(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 1. BES Daily Data":
					obj = dailyEspService.deleteDailyESPByOperationDateService(customUserDetails,
							fileUploads.getOperationDateTime());
					break;	
				case "IMPORT 2. UBH-MTU Hourly Data":
					obj = hourlyHpUBHService.deleteByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 3. HPS Sertecpet Hourly Data":
					obj = hourlyHPSServiceImpl.deleteHourlyHPSByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 4. REINJECTION WELL HPS Hourly Data":
					obj = hourlyReinjectionService.deleteHourlyReinjectionByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 5. DOWNTIME":
					obj = dailyDeferredProductionService.deleteDailyDeferredProductionByOperationDateService(
							customUserDetails, fileUploads.getOperationDateTime());
					break;
				case "IMPORT 6. PRUEBA DE POZOS HORA A HORA":
					obj = hourlyWellTestServiceImpl.deleteHourlyWelltestByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());

					break;
				case "IMPORT 7. CONTROL DE QUIMICA":
//					customResponse = dailyReinjectionService.importDailyReinjectionFromExcel(customUserDetails,
//							multipartFile, operationDateTime);
					break;
				case "IMPORT 8. CONTROL DE CALIDAD":
					obj = hourlyLaboratoryAnalysisASTMService.deleteByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 8-1. CALIDAD WATERCUT":
					obj = hourlyLaboratoryWatercutSerivce.deleteByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				case "IMPORT 9. DAILY TANK MEASUREMENTS":
					obj = dailyDiluentTanklevelAndFiscalizationService
							.deleteDailyDiluentTanklevelAndFiscalizationByOperationDateService(customUserDetails,
									fileUploads.getOperationDateTime());
					break;

				case "IMPORT 9-1. DAILY TANK MEASUREMENTS":
					obj = dispatchTankReportedLevelsService.deleteDispatchTankReportedLevelsByOperationDateService(
							customUserDetails, fileUploads.getOperationDateTime());
					break;
				case "IMPORT 10. RESUMEN DIARIO DE PRODUCCION":

					obj = this.deleteImport10(customUserDetails, fileUploads.getOperationDateTime());

					break;
				case "IMPORT 11. REPORTE DE CARGA Y DESCARGA":
					obj = dailyBargeCrudeUploadDownloadService
							.deleteDailyBargeCrudeUploadDownloadByOperationDateService(customUserDetails,
									fileUploads.getOperationDateTime());
					break;
				case "IMPORT 12. PLANT EVENTS":
					obj = dailyPlantTaskAndTimeService.deleteDailyPlantTasksAndEventsByOperationDateService(
							customUserDetails, fileUploads.getOperationDateTime());
					break;
				case "IMPORT 13. Daily Well Service":
					obj = dailyWellService.deleteDailyWellServiceByOperationDate(customUserDetails,
							fileUploads.getOperationDateTime());
					break;
				default:
					throw new CustomParameterizedException(
							RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
							CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(),
							"category miss match");
				}
				if (StringUtil.haveValue(obj)) {
					fileUploads.setDeleteFlag(true);
					fileUploads = globalFileUploadsRepository.save(fileUploads);
				}
			} else {
				throw new CustomParameterizedException(
						RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
						CustomExceptionCode.FILE_NOT_FOUND_ERR.getErrMsg(),
						CustomExceptionCode.FILE_NOT_FOUND_ERR.getErrCode());
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(), e.getMessage());
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.STOPPED);
		return fileUploads;
	}

	private Object deleteImport10(CustomUserDetails customUserDetails, Date operationDateTime) {
		dailyWellParametersService.deleteByOperationDate(customUserDetails, operationDateTime);
		dailyTankAvailableStorageService.deleteByOperationDate(customUserDetails, operationDateTime);
		dailyWaterTankService.deleteByOperationDate(customUserDetails, operationDateTime);
		dailyRestTanksService.deleteByOperationDate(customUserDetails, operationDateTime);
		dailyChemicalSummaryService.deleteByOperationDate(customUserDetails, operationDateTime);
		List<DailyWaterBalance> dailyWaterBalanceResponse = dailyWaterBalanceService
				.deleteByOperationDate(customUserDetails, operationDateTime);
		return dailyWaterBalanceResponse;

	}

	@Override
	public Object removeAboveDataWithDate(Date operationDateTime, CustomUserDetails customUserDetails) {

		return removeAboveDataOtherthanImportData(operationDateTime, customUserDetails);

		// the below code will not executed as ramana requested to not delete the import
		// data that is why we are calling the aove method

		/*
		 * String methodName = "removeAboveDataWithDate"; log.info(this.getClass() + " "
		 * + methodName + " " + Constants.START); ArrayList<String> collectionNames =
		 * new ArrayList<String>(Arrays.asList("hourly_esp", "hourly_hp_ubh",
		 * "hourly_hps", "hourly_laboratory_analysis_astm",
		 * "hourly_laboratory_water_cut", "hourly_reinjection", "hourly_well_test",
		 * "daily_barge_crude_upload_download", "daily_chemical_summary",
		 * "daily_crude_shipment", "daily_diluent_fiscalization",
		 * "dispatch_tank_reported_levels", "daily_downtime_deferred_production",
		 * "daily_esp", "daily_gas_balance", "daily_hp_ubh", "daily_hps",
		 * "daily_mix_tanklevel_fiscalization", "daily_nafta_upload_download",
		 * "daily_oil_balance", "daily_oil_for_generation",
		 * "daily_plant_task_and_events", "daily_reinjection", "daily_rest_tanks",
		 * "daily_tank_available_storage", "daily_tank_total",
		 * "daily_total_crude_shipment_and_generation",
		 * "daily_total_tank_available_storage",
		 * "daily_total_well_potential_and_production",
		 * "daily_totaldiluenttanklevel_fiscalization",
		 * "daily_totalmixtanklevel_fiscalization", "daily_water_balance",
		 * "daily_water_tank", "daily_well_potential", "daily_well_test",
		 * "diluent_tanks", "dispatch_tanks", "dispatch_tanks_reported",
		 * "daily_well_service", "consolidated_downtime", "esp_diagnostics_data")); try
		 * {
		 * 
		 * Date latestDate = StringUtil.subDays(getLatestDate(customUserDetails), 1);
		 * 
		 * if (operationDateTime.compareTo(latestDate) < 0) { throw new
		 * CustomParameterizedException(RegisteredException.
		 * IMPORTXL_FILES_DATA_EXCEPTION.getException(),
		 * CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REMOVING_COMPLETE_DATA.getErrMsg(
		 * ),
		 * CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REMOVING_COMPLETE_DATA.getErrCode
		 * ()); }
		 * 
		 * Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
		 * Date endDate = StringUtil.setDateBegniningOfTheDayNew(new Date());
		 * 
		 * Query query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("operation_date_time") .gte(startDate).lt(endDate));
		 * 
		 * for (String name : collectionNames) { mongoOperations.remove(query, name); }
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("date_time").gte(startDate) .lt(endDate)); mongoOperations.remove(query,
		 * "daily_well_parameters"); mongoOperations.remove(query,
		 * "db_diluent_unloading_and_fiscaliza"); mongoOperations.remove(query,
		 * "db_heavy_fuel_oil");
		 * 
		 * query = new Query(); query.addCriteria(
		 * Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("fecha").gte(
		 * startDate).lt(endDate)); mongoOperations.remove(query,
		 * "db_consolidated_diluent"); mongoOperations.remove(query, "db_diluent");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("intial_date") .gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "db_dilutent_barges");
		 * mongoOperations.remove(query, "db_oil_barges");
		 * 
		 * query = new Query(); query.addCriteria(
		 * Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date").gte(
		 * startDate).lt(endDate)); mongoOperations.remove(query, "db_down_time");
		 * mongoOperations.remove(query, "db_laboratory"); mongoOperations.remove(query,
		 * "daily_gas_balance");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("mode").is("Generated") .and("date").gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "report_files");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("production_date") .gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "db_monthly_production");
		 * mongoOperations.remove(query, "db_production");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("fiscalization_date") .gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("end_date").gte(startDate) .lt(endDate)); mongoOperations.remove(query,
		 * "db_oil_loading_and_fiscalization"); mongoOperations.remove(query,
		 * "db_heavy_fuel_oil"); mongoOperations.remove(query,
		 * "db_diluent_unloading_and_fiscaliza");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("reinjection_date") .gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "db_reinjection");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("summary_date") .gte(startDate).lt(endDate));
		 * mongoOperations.remove(query, "db_summary");
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("date_time").gte(startDate) .lt(endDate)); mongoOperations.remove(query,
		 * "real_time_solids_monitoring"); mongoOperations.remove(query,
		 * "real_time_solids_monitoring_day_total");
		 * 
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("category").ne("HELP"));
		 * 
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE));
		 * ExtraTableChron chron = mongoOperations.findAndRemove(query,
		 * ExtraTableChron.class); List<Date> dates = new ArrayList<Date>(); if
		 * (StringUtil.haveValue(chron)) { for (Date date : chron.getDates()) { if
		 * (date.before(startDate)) { dates.add(date); } } chron.setDates(dates);
		 * mongoOperations.save(chron); }
		 * 
		 * query = new Query();
		 * query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).
		 * and("operation_date_time") .gte(startDate).lt(endDate));
		 * List<GlobalFileUploads> fileUploads = mongoOperations.find(query,
		 * GlobalFileUploads.class);
		 * 
		 * for (GlobalFileUploads file : fileUploads) { file.setDeletedOn(new Date());
		 * file.setDeleteFlag(true);
		 * 
		 * File destinationFile = new
		 * File(environment.getProperty("spring.history-uploads").split("ROOT")[0] +
		 * "ROOT" + File.separator + file.getFilePath()); if (destinationFile.exists())
		 * { destinationFile.delete(); }
		 * 
		 * } globalFileUploadsRepository.save(fileUploads);
		 * 
		 * String content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n"
		 * + "<body>\n" + "<div style=\"width: 90%; margin: auto\">\n" + "	\n" +
		 * "	<div class=\"graphView\">\n" +
		 * "		<h4> Daily Upload Data  successfully deleted from the following Date "
		 * + StringUtil.convertDateIntoYYMMdd(operationDateTime) +
		 * " to current Date by " + customUserDetails.getUsername() + ".</h4>\n" +
		 * "	</div>	\n" + "</div>\n" + "</body>\n" + "</html>"; String subject =
		 * "Daily Upload Data deleted successfully";
		 * 
		 * reportMailSend(customUserDetails, content, subject);
		 * 
		 * collectionNames = new
		 * ArrayList<String>(Arrays.asList("daily_barge_crude_upload_download",
		 * "dispatch_tank_reported_levels", "daily_chemical_summary",
		 * "daily_crude_shipment", "daily_diluent_fiscalization",
		 * "daily_downtime_deferred_production", "daily_esp", "daily_gas_balance",
		 * "daily_hp_ubh", "daily_hps", "daily_mix_tanklevel_fiscalization",
		 * "daily_nafta_upload_download", "daily_oil_balance",
		 * "daily_oil_for_generation", "daily_plant_task_and_events",
		 * "daily_reinjection", "daily_rest_tanks", "daily_tank_available_storage",
		 * "daily_tank_total", "daily_total_crude_shipment_and_generation",
		 * "daily_total_tank_available_storage",
		 * "daily_total_well_potential_and_production",
		 * "daily_totaldiluenttanklevel_fiscalization",
		 * "daily_totalmixtanklevel_fiscalization", "daily_water_balance",
		 * "daily_water_tank", "daily_well_potential", "daily_well_test",
		 * "diluent_tanks", "dispatch_tanks", "daily_well_parameters",
		 * "db_consolidated_diluent", "db_diluent",
		 * "db_diluent_unloading_and_fiscaliza", "db_dilutent_barges", "db_down_time",
		 * "db_heavy_fuel_oil", "db_laboratory", "db_mapping_issues",
		 * "db_monthly_production", "db_oil_barges", "db_oil_loading_and_fiscalization",
		 * "db_production", "db_reinjection", "db_summary", "global_file_uploads",
		 * "report_files", "dispatch_tanks_reported", "consolidated_downtime",
		 * "real_time_solids_monitoring", "real_time_solids_monitoring_day_total",
		 * "esp_diagnostics_data"));
		 * 
		 * } catch (CustomParameterizedException e) { log.error( this.getClass() + " " +
		 * methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
		 * throw e; } catch (Exception e) { log.error( this.getClass() + " " +
		 * methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
		 * throw new CustomParameterizedException(
		 * RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
		 * CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
		 * CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode()); }
		 * log.info(this.getClass() + " " + methodName + " " + Constants.END); return
		 * collectionNames;
		 */
	}

	@Override
	public Object validateAllFiles(CustomUserDetails customUserDetails, Date operationDateTime) {
		String methodName = "getImportFileNames";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		List<GlobalFileUploads> fileUploads = null;
		try {
			Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
			Date endDate = StringUtil.setDateEndOfTheDay(operationDateTime);
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));
			fileUploads = mongoOperations.find(query, GlobalFileUploads.class);

			for (GlobalFileUploads file : fileUploads) {
				file.setApproved(true);
			}
			globalFileUploadsRepository.save(fileUploads);
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return fileUploads;
	}

	@Override
	public Object removeAboveData(Date operationDateTime, CustomUserDetails customUserDetails) {
		String methodName = "removeAboveData";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		ArrayList<String> collectionNames = new ArrayList<String>(Arrays.asList("hourly_esp", "hourly_hp_ubh",
				"hourly_hps", "hourly_laboratory_analysis_astm", "hourly_laboratory_water_cut", "hourly_reinjection",
				"hourly_well_test", "daily_barge_crude_upload_download", "daily_chemical_summary",
				"daily_crude_shipment", "daily_diluent_fiscalization", "dispatch_tank_reported_levels",
				"daily_downtime_deferred_production", "daily_esp", "daily_gas_balance", "daily_hp_ubh", "daily_hps",
				"daily_mix_tanklevel_fiscalization", "daily_nafta_upload_download", "daily_oil_balance",
				"daily_oil_for_generation", "daily_plant_task_and_events", "daily_reinjection", "daily_rest_tanks",
				"daily_tank_available_storage", "daily_tank_total", "daily_total_crude_shipment_and_generation",
				"daily_total_tank_available_storage", "daily_total_well_potential_and_production",
				"daily_totaldiluenttanklevel_fiscalization", "daily_totalmixtanklevel_fiscalization",
				"daily_water_balance", "daily_water_tank", "daily_well_potential", "diluent_tanks", "dispatch_tanks",
				"dispatch_tanks_reported", "daily_well_service", "consolidated_downtime", "esp_diagnostics_data"));
		try {

			Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
			Date endDate = StringUtil.setDateBegniningOfTheDayNew(new Date());

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));

			for (String name : collectionNames) {
				mongoOperations.remove(query, name);
			}

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date_time").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "daily_well_parameters");
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");
			mongoOperations.remove(query, "db_heavy_fuel_oil");

			query = new Query();
			query.addCriteria(
					Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("fecha").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_consolidated_diluent");
			mongoOperations.remove(query, "db_diluent");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("intial_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_dilutent_barges");
			mongoOperations.remove(query, "db_oil_barges");

			query = new Query();
			query.addCriteria(
					Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_down_time");
			mongoOperations.remove(query, "db_laboratory");
			mongoOperations.remove(query, "daily_gas_balance");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("mode").is("Generated")
					.and("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "report_files");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("production_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_monthly_production");
			mongoOperations.remove(query, "db_production");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("fiscalization_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			query = new Query();

			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("end_date").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			mongoOperations.remove(query, "db_heavy_fuel_oil");
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("reinjection_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_reinjection");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("summary_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_summary");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date_time").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "real_time_solids_monitoring");
			mongoOperations.remove(query, "real_time_solids_monitoring_day_total");
			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE));
			ExtraTableChron chron = mongoOperations.findAndRemove(query, ExtraTableChron.class);
			List<Date> dates = new ArrayList<Date>();
			if (StringUtil.haveValue(chron)) {
				for (Date date : chron.getDates()) {
					if (date.before(startDate)) {
						dates.add(date);
					}
				}
				chron.setDates(dates);
				mongoOperations.save(chron);
			}

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));
			List<GlobalFileUploads> fileUploads = mongoOperations.find(query, GlobalFileUploads.class);

			for (GlobalFileUploads file : fileUploads) {
				file.setApproved(false);
				file.setProcessed(false);
			}
			globalFileUploadsRepository.save(fileUploads);

			collectionNames = new ArrayList<String>(Arrays.asList("daily_barge_crude_upload_download",
					"dispatch_tank_reported_levels", "daily_chemical_summary", "daily_crude_shipment",
					"daily_diluent_fiscalization", "daily_downtime_deferred_production", "daily_esp",
					"daily_gas_balance", "daily_hp_ubh", "daily_hps", "daily_mix_tanklevel_fiscalization",
					"daily_nafta_upload_download", "daily_oil_balance", "daily_oil_for_generation",
					"daily_plant_task_and_events", "daily_reinjection", "daily_rest_tanks",
					"daily_tank_available_storage", "daily_tank_total", "daily_total_crude_shipment_and_generation",
					"daily_total_tank_available_storage", "daily_total_well_potential_and_production",
					"daily_totaldiluenttanklevel_fiscalization", "daily_totalmixtanklevel_fiscalization",
					"daily_water_balance", "daily_water_tank", "daily_well_potential", "daily_well_test",
					"diluent_tanks", "dispatch_tanks", "daily_well_parameters", "db_consolidated_diluent", "db_diluent",
					"db_diluent_unloading_and_fiscaliza", "db_dilutent_barges", "db_down_time", "db_heavy_fuel_oil",
					"db_laboratory", "db_mapping_issues", "db_monthly_production", "db_oil_barges",
					"db_oil_loading_and_fiscalization", "db_production", "db_reinjection", "db_summary",
					"global_file_uploads", "report_files", "dispatch_tanks_reported", "consolidated_downtime",
					"real_time_solids_monitoring", "real_time_solids_monitoring_day_total", "esp_diagnostics_data"));

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return collectionNames;

	}

	@Override
	public Object removeDataBaseData(SearchVM searchVM, CustomUserDetails customUserDetails) {
		String methodName = "removeAboveData";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);

		try {
			Map<String, String> collMap = new HashMap<>();
			if (StringUtil.haveValue(searchVM.getCollection())) {
				collMap = searchVM.getCollection();
				for (String collectionName : collMap.keySet()) {
					Query query = new Query();
					if (StringUtil.haveValue(searchVM.getDate1()) && StringUtil.haveValue(searchVM.getDate2())) {
						query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE)
								.and(collMap.get(collectionName)).gte(searchVM.getDate1()).lte(searchVM.getDate2()));
					} else {
						query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE));
					}
					mongoOperations.remove(query, collectionName);
				}
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return searchVM.getCollectionName();
	}

	@Override
	public Object uploadTempleteAndManualFle(MultipartFile multipartFile, String category, String startDate,
			CustomUserDetails customUserDetails) {
		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();

		GlobalFileUploads fileUploads = null;
		File destinationFile = null;
		Double index = 0.0;
		try {
			String uploadPath = environment.getProperty("spring.upload-path") + "/" + category;
			String historyPath = environment.getProperty("spring.history-uploads");
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("file_name")
					.is(multipartFile.getOriginalFilename()).and("category").is(category));
			switch (category) {
			case "HELP":
				File destinationFolderManual = new File(historyPath + "/help");
				if (!destinationFolderManual.exists()) {
					destinationFolderManual.mkdirs();
				}
				destinationFile = new File(destinationFolderManual.getPath(), multipartFile.getOriginalFilename());
				if (destinationFile.exists()) {
					mongoOperations.remove(query, GlobalFileUploads.class);
					destinationFile.delete();
				}
				break;
			default:
				File destinationFolderTemplete = new File(uploadPath);
				if (!destinationFolderTemplete.exists()) {
					destinationFolderTemplete.mkdirs();
				}
				destinationFile = new File(destinationFolderTemplete.getPath(), multipartFile.getOriginalFilename());
				if (destinationFile.exists()) {
					mongoOperations.remove(query, GlobalFileUploads.class);
					destinationFile.delete();
				}
				break;
			}

			Date sDate = StringUtil.convertSimpleStringToDate(startDate);

			if (!multipartFile.isEmpty()) {
				multipartFile.transferTo(destinationFile);
				if (destinationFile.exists()) {
					Path path = Paths.get(destinationFile.getPath());
					Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
				}

				GlobalFileUploads previousFile = globalFileUploadsRepository
						.findOneByCategoryAndDeleteFlagAndStartDateLessThanOrderByStartDateDesc(category, false, sDate);
				if (StringUtil.haveValue(previousFile)) {
					previousFile.setEndDate(StringUtil.subDays(sDate, 1));
					globalFileUploadsRepository.save(previousFile);
				}

				fileUploads = new GlobalFileUploads();
				fileUploads.setFileName(multipartFile.getOriginalFilename());
				fileUploads.setCategory(category);
				fileUploads.setStartDate(sDate);
				fileUploads.setFilePath(destinationFile.getAbsolutePath()
						.split("ROOT")[destinationFile.getAbsolutePath().split("ROOT").length - 1]);
				mongoTemplate.save(fileUploads);
			}
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		return fileUploads;
	}

	@Override
	public Date getLatestDate(CustomUserDetails customUserDetails) {
		String methodName = "getImportFileNames";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		List<String> importNames;
		Date latestDate = null;
		try {
			GlobalFileUploads latestrecord = globalFileUploadsRepository
					.findOneByDeleteFlagAndIsProcessedOrderByOperationDateTimeDesc(false, true);
			if (!StringUtil.haveValue(latestrecord)) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrMsg(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());

			} else {
				latestDate = latestrecord.getOperationDateTime();
				latestDate = StringUtil.addDays(latestDate, 1);
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public Object automaticProcess(SearchVM searchVM, CustomUserDetails customUserDetails) {
		String methodName = "automaticProcess";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Date latestDate = null;
		List<GlobalFileUploads> fileUploads = null;

		try {

			int totalcount = getGlobalImportDropdownList(customUserDetails).getTotalCount();

			Date startDate = StringUtil
					.setDateBegniningOfTheDayNew(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()));
			Date endDate = StringUtil.setDateEndOfTheDay(StringUtil.convertSimpleStringToDate(searchVM.getEndDate()));

			removeAboveData(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()), customUserDetails);

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lte(endDate));
			fileUploads = mongoOperations.find(query, GlobalFileUploads.class);
			TreeMap<Date, List<GlobalFileUploads>> dayFiles = fileUploads.stream().collect(
					Collectors.groupingBy(GlobalFileUploads::getOperationDateTime, TreeMap::new, Collectors.toList()));

			String dateString = "";
			for (Date date : dayFiles.keySet()) {
				if (dayFiles.get(date).size() < totalcount) {
					if (dateString.length() > 0)
						dateString = dateString + ", " + StringUtil.convertDateIntoYYMMdd(date);
					else
						dateString = StringUtil.convertDateIntoYYMMdd(date);
				}
			}
			if (dateString.length() > 0) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						CustomExceptionCode.FILE_DATA_NOT_FOUND.getErrMsg()
								+ "Required More Files to Process the Imports On " + dateString,
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
			}

			for (Date date : dayFiles.keySet()) {
				List<GlobalFileUploads> fileUploadsList = dayFiles.get(date);
				fileUploadsList.sort((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()));

				for (GlobalFileUploads file : fileUploadsList) {
					File destinationFile = new File(environment.getProperty("spring.history-uploads").split("ROOT")[0]
							+ "ROOT" + File.separator + file.getFilePath());
					if (!destinationFile.exists()) {
						throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
								CustomExceptionCode.FILE_DATA_NOT_FOUND.getErrMsg() + " " + file.getCategory() + " On "
										+ StringUtil.convertDateIntoYYMMdd(file.getOperationDateTime()),
								CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
					}

					FileInputStream input = new FileInputStream(destinationFile);
					MultipartFile multipartFile1 = new MockMultipartFile("file", destinationFile.getName(),
							"text/plain", IOUtils.toByteArray(input));

					automaticUploadFile(multipartFile1, file.getCategory(), file.getOperationDateTime(),
							customUserDetails);
				}
				chronProcessedWithDateWithOutVfm(customUserDetails, date);
				// Thread.sleep(30000);
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public CustomResponse automaticUploadFile(MultipartFile multipartFile, String category, Date operationDateTime,
			CustomUserDetails customUserDetails) {

		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();

		CustomResponse customResponse = null;
		try {
			switch (category) {
			case "IMPORT 1. BES Hourly Data":
				customResponse = espService.importHourlyEspFromExcel(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 2. UBH-MTU Hourly Data":
				customResponse = hourlyHpUBHService.importHourlyHpUBHServiceFromExcel(customUserDetails, multipartFile,
						operationDateTime);
				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyHpUBH>) customResponse.getData(), HourlyHpUBH.class);
				}
				break;
			case "IMPORT 3. HPS Sertecpet Hourly Data":
				customResponse = hourlyHPSServiceImpl.importHourlyHps(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 4. REINJECTION WELL HPS Hourly Data":
				customResponse = hourlyReinjectionService.importReinjection(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 5. DOWNTIME":
				customResponse = dailyDeferredProductionService.importDowntimeSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 6. PRUEBA DE POZOS HORA A HORA":
				customResponse = hourlyWellTestServiceImpl.importHourlywelltest(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 7. CONTROL DE QUIMICA":
				customResponse = dailyReinjectionService.importDailyReinjectionFromExcel(customUserDetails,
						multipartFile, operationDateTime);
				break;
			case "IMPORT 8. CONTROL DE CALIDAD":
				String dat = "2023-03-09 00:00:00.0";
				Date dat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dat);
				if (operationDateTime.before(dat1)) {
					customResponse = hourlyLaboratoryAnalysisASTMService.importHourlyLaboratoryAnalysisASTMFromExcel(
							customUserDetails, multipartFile, operationDateTime);
				} else {
					customResponse = hourlyLaboratoryAnalysisASTMService.importHourlyLaboratoryAnalysisASTMFromExcelNew(
							customUserDetails, multipartFile, operationDateTime);
				}

				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyLaboratoryAnalysisASTM>) customResponse.getData(),
							HourlyLaboratoryAnalysisASTM.class);
				}
				break;
			case "IMPORT 8-1. CALIDAD WATERCUT":
				customResponse = hourlyLaboratoryWatercutSerivce
						.importHourlyLaboratoryWaterCutFromExcel(customUserDetails, multipartFile, operationDateTime);
				if (customResponse.isSuccess()) {
					mongoOperations.insert((List<HourlyLaboratoryWatercut>) customResponse.getData(),
							HourlyLaboratoryWatercut.class);
				}
				break;
			case "IMPORT 9. DAILY TANK MEASUREMENTS":
				customResponse = dailyDiluentTanklevelAndFiscalizationService
						.importDailyDiluentTanklevelAndFiscalizationSheet(customUserDetails, multipartFile,
								operationDateTime);
				if (!customResponse.isSuccess()) {
					dailyDiluentTanklevelAndFiscalizationService
							.deleteDailyDiluentTanklevelAndFiscalizationByOperationDateService(customUserDetails,
									operationDateTime);
				}

				break;
			case "IMPORT 9-1. DAILY TANK MEASUREMENTS":
				customResponse = dispatchTankReportedLevelsService
						.importDispatchTankReportedLevelsSheet(customUserDetails, multipartFile, operationDateTime);
				if (!customResponse.isSuccess()) {
					dispatchTankReportedLevelsService.deleteDispatchTankReportedLevelsByOperationDateService(
							customUserDetails, operationDateTime);
				}
				break;
			case "IMPORT 10. RESUMEN DIARIO DE PRODUCCION":
				String oldstring = "2022-04-05 00:00:00.0";
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(oldstring);
				if (operationDateTime.before(date)) {
					customResponse = this.import10FromExcel(customUserDetails, multipartFile, operationDateTime,
							metaInfo);
				} else {
					customResponse = this.import10FromExcelNew(customUserDetails, multipartFile, operationDateTime,
							metaInfo);
				}
				break;
			case "IMPORT 11. REPORTE DE CARGA Y DESCARGA":
				customResponse = hidrocarbonsService.importHidrocarbonsServiceSheet(customUserDetails, multipartFile,
						operationDateTime);
				customResponse = dailyBargeCrudeUploadDownloadService
						.importDailyBargeCrudeUploadDownloadSheet(customUserDetails, multipartFile, operationDateTime);

				if (!customResponse.isSuccess()) {
					dailyBargeCrudeUploadDownloadService.deleteDailyBargeCrudeUploadDownloadByOperationDateService(
							customUserDetails, operationDateTime);
					Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
					Date endDate = StringUtil.setDateEndOfTheDayNew(operationDateTime);
					Query query = new Query();
					query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("end_date")
							.gte(startDate).lt(endDate));
					mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
					mongoOperations.remove(query, "db_heavy_fuel_oil");
					mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");
				}
				break;
			case "IMPORT 12. PLANT EVENTS":
				customResponse = dailyPlantTaskAndTimeService.importPlantEventSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;
			case "IMPORT 13. Daily Well Service":
				customResponse = dailyWellService.importDailyWellServiceSheet(customUserDetails, multipartFile,
						operationDateTime);
				break;
			default:
				customResponse = new CustomResponse(Boolean.TRUE,
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
						CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
						"category miss match", metaInfo);
			}
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			return new CustomResponse(Boolean.FALSE,
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
					metaInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return new CustomResponse(Boolean.FALSE,
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
					metaInfo);
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		return customResponse;
	}

	@Override
	public Object chronProcessedWithDateWithOutVfm(CustomUserDetails customUserDetails, Date operationDateTime) {
		String methodName = "chronProcessedWithDate";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		try {

			// It is to update whenever there is changes in hourly well test or downtime or
			// well potential adusted values from import 10 sheet 1
			dailyWellPotentialService.updateAllDailyWellPotentialAdjustedValues(customUserDetails, operationDateTime);

			String dates = "2023-08-01 00:00:00.0";
			Date dates1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dates);
			if (operationDateTime.before(dates1)) {
				// chron 1
				DailyEsp esp = new DailyEsp();
				esp.setOperationDateTime(operationDateTime);
				dailyEspService.addDailyEsp(customUserDetails, esp);
			}

			// chron 2
			DailyHpUBH dailyHpUBH = new DailyHpUBH();
			dailyHpUBH.setOperationDateTime(operationDateTime);
			dailyHpUBHService.addDailyHpUBH(customUserDetails, dailyHpUBH);

			// chron 3
			DailyHPS dailyHPS = new DailyHPS();
			dailyHPS.setOperationDateTime(operationDateTime);
			dailyHPSService.addDaillyHPS(customUserDetails, dailyHPS);

			// chron 4
			DailyReinjection dailyReinjection = new DailyReinjection();
			dailyReinjection.setOperationDateTime(operationDateTime);
			dailyReinjectionService.addDailyReinjection(customUserDetails, dailyReinjection);

			// chron 5
			/*
			 * DailyWellTest
			 */

			// chron 6
			DailyWellParameters dailyWellParameters = new DailyWellParameters();
			dailyWellParameters.setDateTime(operationDateTime);
			dailyWellParametersService.addDailyWellParameters(customUserDetails, dailyWellParameters);

			// chron 7
			DailyWellPotential dailyWellPotential = new DailyWellPotential();
			dailyWellPotential.setOperationDateTime(operationDateTime);
			dailyWellPotentialService.addDailyWellPotentialProduction(customUserDetails, dailyWellPotential);

			// chron 8
			DailyTotalWellPotentialAndProduction dailyTotalWellPotentialAndProduction = new DailyTotalWellPotentialAndProduction();
			dailyTotalWellPotentialAndProduction.setOperationDateTime(operationDateTime);
			dailyTotalWellPotentialAndProductionService.saveDailyTotalWellPAP(customUserDetails,
					dailyTotalWellPotentialAndProduction);

			// chron 9
			DailyTotalmixtanklevelAndFiscalization dailyTotalmixtanklevelAndFiscalization = new DailyTotalmixtanklevelAndFiscalization();
			dailyTotalmixtanklevelAndFiscalization.setOperationDateTime(operationDateTime);
			dailyTotalmixtanklevelAndFiscalizationService.addDailyTotalmixtanklevelAndFiscalization(customUserDetails,
					dailyTotalmixtanklevelAndFiscalization);

			// chron 10
			DispatchTanks dispatchTanks = new DispatchTanks();
			dispatchTanks.setOperationDateTime(operationDateTime);
			dispatchTanksService.addDispatchTanks(customUserDetails, dispatchTanks);

			// chron 10-1
			/*
			 * DispatchTanksReported dispatchTanksReported = new DispatchTanksReported();
			 * dispatchTanksReported.setOperationDateTime(operationDateTime);
			 * dispatchTanksReportedService.addDispatchTanksReported(customUserDetails,
			 * dispatchTanksReported);
			 */
			// chron 11
			DailyTotalTankAvailableStorage dailyTotalTankAvailableStorage = new DailyTotalTankAvailableStorage();
			dailyTotalTankAvailableStorage.setOperationDateTime(operationDateTime);
			dailyTotalTankAvailableStorageService.addDailyTotalTankAvailableStorage(customUserDetails,
					dailyTotalTankAvailableStorage);

			// chron 12
			DailyTotalDiluentTankLevelAndFiscalization dailyTotalDiluentTankLevelAndFiscalization = new DailyTotalDiluentTankLevelAndFiscalization();
			dailyTotalDiluentTankLevelAndFiscalization.setOperationDateTime(operationDateTime);
			dailyTotalDiluentTankLevelAndFiscalizationService.addDailyTotalDiluentTankLevelAndFiscalization(
					customUserDetails, dailyTotalDiluentTankLevelAndFiscalization);

			// chron 13
			DiluentTanks diluentTanks = new DiluentTanks();
			diluentTanks.setOperationDateTime(operationDateTime);
			diluentTanksService.addDiluentTanks(customUserDetails, diluentTanks);

			// chron 14
			DailyTankTotals dailyTankTotals = new DailyTankTotals();
			dailyTankTotals.setOperationDateTime(operationDateTime);
			dailyTankTotalService.addDailyTankTotal(customUserDetails, dailyTankTotals);

			// chron 15 doubt EMBARQUE is for entire table or for operation number
			DailyCrudeShipment dailyCrudeShipment = new DailyCrudeShipment();
			dailyCrudeShipment.setOperationDateTime(operationDateTime);
			dailyCrudeShipmentService.addDailyCrudeShipment(customUserDetails, dailyCrudeShipment);

			// chron 16
			DailyOilForGeneration dailyOilForGeneration = new DailyOilForGeneration();
			dailyOilForGeneration.setOperationDateTime(operationDateTime);
			dailyOilForGenerationService.addDailyOilForGeneration(customUserDetails, dailyOilForGeneration);

			// chron 17
			DtCrudeShipmentAndGeneration dtCrudeShipmentAndGeneration = new DtCrudeShipmentAndGeneration();
			dtCrudeShipmentAndGeneration.setOperationDateTime(operationDateTime);
			dtCrudeShipmentAndGenerationService.addDtCrudeShipmentAndGeneration(customUserDetails,
					dtCrudeShipmentAndGeneration);

			// chron 18 need to know Stock Skimmed Oil and Real Stock Skimmed Oil
			DailyOilBalance dailyOilBalance = new DailyOilBalance();
			dailyOilBalance.setOperationDateTime(operationDateTime);
			dailyOilBalanceService.addDailyOilBalance(customUserDetails, dailyOilBalance);

			// newly added ConsolidatedDownTime

			ConsolidatedDownTime consolidatedDownTime = new ConsolidatedDownTime();
			consolidatedDownTime.setOperationDateTime(operationDateTime);
			consolidatedDownTimeService.addConsolidatedDownTime(customUserDetails, consolidatedDownTime);

			// chron 19
			Production production = new Production();
			production.setProductionDate(operationDateTime);
			List<Production> productionList = productionService.saveProduction(production, customUserDetails);

			// chron 20
			MonthlyProduction monthlyProduction = new MonthlyProduction();
			monthlyProduction.setProductionDate(operationDateTime);
			productionService.saveMonthlyProduction(monthlyProduction, customUserDetails);

			// chron 21
			Reinjection reinjection = new Reinjection();
			reinjection.setReinjectionDate(operationDateTime);
			reinjectionService.saveReinjection(reinjection, customUserDetails);

			// chron 22
			OilLoadingandFiscalization oilLoadingandFiscalization = new OilLoadingandFiscalization();
			oilLoadingandFiscalization.setFiscalizationDate(operationDateTime);
			oilLoadingandFiscalizationService.saveFiscalizationData(oilLoadingandFiscalization, customUserDetails);

			// chron 23
			DbDiluentUnloadingAndFiscaliza dbDiluentUnloadingAndFiscaliza = new DbDiluentUnloadingAndFiscaliza();
			dbDiluentUnloadingAndFiscaliza.setDateTime(operationDateTime);
			dbDiluentUnloadingAndFiscalizaService.addDbDiluentUnloadingAndFiscaliza(customUserDetails,
					dbDiluentUnloadingAndFiscaliza);

			// chron 24
			DbHeavyFuelOil dbHeavyFuelOil = new DbHeavyFuelOil();
			dbHeavyFuelOil.setDateTime(operationDateTime);
			dbHeavyFuelOilService.addDbHeavyFuelOil(customUserDetails, dbHeavyFuelOil);

			// chron 25
			DbOilBarges dbOilBarges = new DbOilBarges();
			dbOilBarges.setIntialDate(operationDateTime);
			dbOilBargesService.addDbOilBarges(customUserDetails, dbOilBarges);

			// chron 26
			DbDilutentBarges dbDilutentBarges = new DbDilutentBarges();
			dbDilutentBarges.setIntialDate(operationDateTime);
			dbDilutentBargesService.addDbDilutentBarges(customUserDetails, dbDilutentBarges);

			// chron 27
			DbLaboratory dbLaboratory = new DbLaboratory();
			dbLaboratory.setDate(operationDateTime);
			laboratoryService.addLaboratory(customUserDetails, dbLaboratory);

			// chron 28
			DbDiluent dbDiluent = new DbDiluent();
			dbDiluent.setFecha(operationDateTime);
			diluentService.addDaillyHPS(customUserDetails, dbDiluent);

			// chron 29
			ConsolidatedDiluent consolidatedDiluent = new ConsolidatedDiluent();
			consolidatedDiluent.setFecha(operationDateTime);
			consolidatedDiluentService.addDaillyHPS(customUserDetails, consolidatedDiluent);

			// chron 30
			DbDownTime dbDownTime = new DbDownTime();
			dbDownTime.setDate(operationDateTime);
			downtimeService.addDbDownTime(customUserDetails, dbDownTime);

			// chron 31 00 petrotal file all are user entered ChemicalTreatment

			// chron 32
			Summary summary = new Summary();
			summary.setSummaryDate(operationDateTime);
			summaryService.saveSummaryData(summary, customUserDetails);

			List<GlobalFileUploads> gobalList = globalFileUploadsRepository.findByDeleteFlagAndOperationDateTimeBetween(
					false, StringUtil.setDateBegniningOfTheDayNew(operationDateTime),
					StringUtil.setDateEndOfTheDayNew(operationDateTime));

			for (GlobalFileUploads files : gobalList) {
				files.setApproved(true);
				files.setProcessed(true);

			}
			globalFileUploadsRepository.save(gobalList);

			ExtraTableChron extraTableChron = new ExtraTableChron();
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			extraTableChron = mongoOperations.findOne(query, ExtraTableChron.class);
			if (StringUtil.haveValue(extraTableChron)) {
				if (!extraTableChron.getDates().contains(operationDateTime))
					extraTableChron.getDates().add(operationDateTime);
			} else {
				extraTableChron = new ExtraTableChron();
				List<Date> date = new ArrayList<Date>();
				date.add(operationDateTime);
				extraTableChron.setDates(date);
			}
			mongoOperations.save(extraTableChron);
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return true;
	}

	@Override
	public Object onChronProcessFailedRemoveData(Date operationDateTime, CustomUserDetails customUserDetails) {
		String methodName = "onChronProcessFailedRemoveData";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		ArrayList<String> collectionNames = new ArrayList<String>(Arrays.asList("daily_esp", "daily_hp_ubh",
				"daily_hps", "daily_reinjection", "daily_well_potential", "daily_mix_tanklevel_fiscalization",
				"daily_total_well_potential_and_production", "daily_totalmixtanklevel_fiscalization", "dispatch_tanks",
				"dispatch_tanks_reported", "daily_total_tank_available_storage",
				"daily_totaldiluenttanklevel_fiscalization", "diluent_tanks", "daily_tank_total",
				"daily_crude_shipment", "daily_oil_for_generation", "daily_total_crude_shipment_and_generation",
				"daily_oil_balance", "consolidated_downtime", "esp_diagnostics_data"));
		try {

			Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
			Date endDate = StringUtil.setDateBegniningOfTheDayNew(new Date());

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));

			for (String name : collectionNames) {
				mongoOperations.remove(query, name);
			}

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date_time").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");
			mongoOperations.remove(query, "db_heavy_fuel_oil");

			query = new Query();
			query.addCriteria(
					Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("fecha").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_consolidated_diluent");
			mongoOperations.remove(query, "db_diluent");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("intial_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_dilutent_barges");
			mongoOperations.remove(query, "db_oil_barges");

			query = new Query();
			query.addCriteria(
					Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_down_time");
			mongoOperations.remove(query, "db_laboratory");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("mode").is("Generated")
					.and("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "report_files");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("production_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_monthly_production");
			mongoOperations.remove(query, "db_production");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("fiscalization_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			query = new Query();

			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("end_date").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			mongoOperations.remove(query, "db_heavy_fuel_oil");
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("reinjection_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_reinjection");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("summary_date")
					.gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_summary");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("date_time").gte(startDate)
					.lt(endDate));
			mongoOperations.remove(query, "real_time_solids_monitoring");
			mongoOperations.remove(query, "real_time_solids_monitoring_day_total");

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE));
			ExtraTableChron chron = mongoOperations.findAndRemove(query, ExtraTableChron.class);
			List<Date> dates = new ArrayList<Date>();
			if (StringUtil.haveValue(chron)) {
				for (Date date : chron.getDates()) {
					if (date.before(startDate)) {
						dates.add(date);
					}
				}
				chron.setDates(dates);
				mongoOperations.save(chron);
			}
			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));
			List<GlobalFileUploads> fileUploads = mongoOperations.find(query, GlobalFileUploads.class);

			for (GlobalFileUploads file : fileUploads) {
				file.setApproved(true);
				file.setProcessed(false);
			}
			globalFileUploadsRepository.save(fileUploads);

			collectionNames = new ArrayList<String>(Arrays.asList("daily_barge_crude_upload_download",
					"dispatch_tank_reported_levels", "daily_chemical_summary", "daily_crude_shipment",
					"daily_diluent_fiscalization", "daily_downtime_deferred_production", "daily_esp",
					"daily_gas_balance", "daily_hp_ubh", "daily_hps", "daily_mix_tanklevel_fiscalization",
					"daily_nafta_upload_download", "daily_oil_balance", "daily_oil_for_generation",
					"daily_plant_task_and_events", "daily_reinjection", "daily_rest_tanks",
					"daily_tank_available_storage", "daily_tank_total", "daily_total_crude_shipment_and_generation",
					"daily_total_tank_available_storage", "daily_total_well_potential_and_production",
					"daily_totaldiluenttanklevel_fiscalization", "daily_totalmixtanklevel_fiscalization",
					"daily_water_balance", "daily_water_tank", "daily_well_potential", "daily_well_test",
					"diluent_tanks", "dispatch_tanks", "daily_well_parameters", "db_consolidated_diluent", "db_diluent",
					"db_diluent_unloading_and_fiscaliza", "db_dilutent_barges", "db_down_time", "db_heavy_fuel_oil",
					"db_laboratory", "db_mapping_issues", "db_monthly_production", "db_oil_barges",
					"db_oil_loading_and_fiscalization", "db_production", "db_reinjection", "db_summary",
					"global_file_uploads", "report_files", "dispatch_tanks_reported", "consolidated_downtime",
					"real_time_solids_monitoring", "real_time_solids_monitoring_day_total", "esp_diagnostics_data"));

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return collectionNames;
	}

	@Override
	public Object chronProcessedForExtraTable(CustomUserDetails customUserDetails) {
		String methodName = "onChronProcessFailedRemoveData";
		try {
			ExtraTableChron extraTableChron = new ExtraTableChron();
			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(false));
			extraTableChron = mongoOperations.findAndRemove(query, ExtraTableChron.class);
			if (StringUtil.haveValue(extraTableChron)) {
				List<Date> dates = new ArrayList<Date>();
				dates.addAll(extraTableChron.getDates());
				Collections.sort(dates);
				for (Date operationDateTime : dates) {

					// calculating pdc automatically
					DailyWellTest dailyWellTest = new DailyWellTest();
					dailyWellTest.setOperationDateTime(operationDateTime);
					dailyWellTestService.calculatePdc(customUserDetails, dailyWellTest);

					Map<String, Map<String, Double>> finalMap = new HashMap<String, Map<String, Double>>();
					Map<String, Double> wellMap = new HashMap<>();
					Map<String, Double> prodMap = new HashMap<>();
					Map<String, Double> dateMap = new HashMap<>();

					List<Production> productionList = mongoOperations.find(
							StringUtil.getQueryOnSelectedDate("productionDate", operationDateTime), Production.class);

					for (Production produ : productionList) {
						query = new Query();
						query.addCriteria(
								Criteria.where(Constants.DELETE_FLAG).is(false).and("well").is(produ.getWell()));
						query.fields().include("adjusted_oil_flowrate");
						query.with(new Sort(Sort.Direction.DESC, "operation_date_time"));
						DailyWellTest dWellTest = mongoOperations.findOne(query, DailyWellTest.class);
						if (StringUtil.haveValue(dWellTest)
								&& StringUtil.haveValue(dWellTest.getAdjustedOilFlowrate())) {
							wellMap.put(produ.getWell(), dWellTest.getAdjustedOilFlowrate());
						}
						if (StringUtil.haveValue(produ.getOil())) {
							prodMap.put(produ.getWell(), produ.getOil());
						}
					}
					if (!wellMap.isEmpty())
						finalMap.put("WellTestOil", wellMap);
					if (!prodMap.isEmpty())
						finalMap.put("ProductionOil", prodMap);
					finalMap.put(StringUtil.convertDateIntoYYMMdd(operationDateTime), dateMap);
					try {
						feignClientForDeviceManagementSystem.updateVfmTable(customUserDetails.getToken(), finalMap);
						realTimeSolidsMonitoringService.everyHourCronForRealTimeSolidsMonitoring(customUserDetails,
								StringUtil.convertDateIntoYYMMdd(operationDateTime));
					} catch (Exception e) {
						// ignore
					}
					// to generate the esp diagnostic data

					try {
						EspDiagnosticsData espDiagnosticsData = new EspDiagnosticsData();
						espDiagnosticsData.setOperationDateTime(operationDateTime);
						espDiagnosticsDataService.saveAllEspDiagnosticsData(customUserDetails, espDiagnosticsData);
					} catch (Exception e) {
					}
				}
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return true;
	}

	@Override
	public Object reportMailSend(CustomUserDetails customUserDetails, String content, String subject) {
		String methodName = "reportMailSend";
		AdvanceSearchList htmlPdfConversionList = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("operation").is("UplodedProcess").and(Constants.DELETE_FLAG).is(false));
			EmailRecipients emailRecipients = mongoOperations.findOne(query, EmailRecipients.class);
			if (StringUtil.haveValue(emailRecipients)) {
				EmailMV emailMV = new EmailMV();
				emailMV.setTo(emailRecipients.getRecipients());
				emailMV.setSubject(subject);
				emailMV.setMultipart(false);
				emailMV.setHtml(true);
				emailMV.setContent(content);
				genericservices.sendEmailWithAttachment(emailMV);
			}

		} catch (CustomParameterizedException customException) {
			customException.printStackTrace();
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ "
					+ customException.getMessage() + " ]");
			throw customException;
		} catch (Exception exception) {
			log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + exception.getMessage()
					+ " ]");
			throw new CustomParameterizedException(RegisteredException.HTML_PDF_CONVERSION_EXCEPTION.getException(),
					CustomExceptionCode.HTML_PDF_CONVERSION_FETCH_EXCEPTION.getErrMsg(),
					CustomExceptionCode.HTML_PDF_CONVERSION_FETCH_EXCEPTION.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return htmlPdfConversionList;
	}

	@Override
	public Object deleteByFileUploadId(String globalFileUploadsId, CustomUserDetails customUserDetails) {
		String methodName = "deleteByFileUploadId";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();
		GlobalFileUploads fileUploads = new GlobalFileUploads();
		Object obj = null;
		try {
			fileUploads = globalFileUploadsRepository.findByGlobalFileUploadsIdAndDeleteFlag(globalFileUploadsId,
					false);

			if (StringUtil.haveValue(fileUploads)) {
				File destinationFile = new File(environment.getProperty("spring.upload-path").split("ROOT")[0] + "ROOT"
						+ fileUploads.getFilePath());
				if (destinationFile.exists()) {
					destinationFile.delete();
				}
				if (StringUtil.haveValue(fileUploads)) {
					fileUploads.setDeleteFlag(true);
					fileUploads = globalFileUploadsRepository.save(fileUploads);
				}
			} else {
				throw new CustomParameterizedException(
						RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
						CustomExceptionCode.FILE_NOT_FOUND_ERR.getErrMsg(),
						CustomExceptionCode.FILE_NOT_FOUND_ERR.getErrCode());
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_REJECTING_THE_FILES_.getErrCode(), e.getMessage());
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.STOPPED);
		return fileUploads;
	}

	@Override
	public Object updateTempleteFile(CustomUserDetails customUserDetails, String id,
			GlobalFileUploadVM globalFileUploadVM) {
		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();

		GlobalFileUploads fileUploads = null;
		File destinationFile, destinationFileOld = null;
		Double index = 0.0;
		try {
			GlobalFileUploads globalFileUploads = globalFileUploadsRepository.findByGlobalFileUploadsIdAndDeleteFlag(id,
					false);
			if (StringUtil.haveValue(globalFileUploads)) {
				if (StringUtil.haveValue(globalFileUploadVM.getStartDate()))
					globalFileUploads
							.setStartDate(StringUtil.convertSimpleStringToDate(globalFileUploadVM.getStartDate()));
				if (StringUtil.haveValue(globalFileUploadVM.getEndDate()))
					globalFileUploads.setEndDate(StringUtil.convertSimpleStringToDate(globalFileUploadVM.getEndDate()));
				if (StringUtil.haveValue(globalFileUploadVM.getMultipartFile())
						&& !globalFileUploadVM.getMultipartFile().isEmpty()) {
					String uploadPath = environment.getProperty("spring.upload-path").split("ROOT")[0] + "ROOT/"
							+ globalFileUploads.getCategory();
					File destinationFolderTemplete = new File(uploadPath);
					if (!destinationFolderTemplete.exists()) {
						destinationFolderTemplete.mkdirs();
					}
					destinationFileOld = new File(destinationFolderTemplete.getPath(), globalFileUploads.getFileName());
					if (destinationFileOld.exists()) {
						destinationFileOld.delete();
					}
					destinationFile = new File(destinationFolderTemplete.getPath(),
							globalFileUploadVM.getMultipartFile().getOriginalFilename());
					if (destinationFile.exists()) {
						destinationFile.delete();
					}

					globalFileUploadVM.getMultipartFile().transferTo(destinationFile);
					if (destinationFile.exists()) {
						Path path = Paths.get(destinationFile.getPath());
						Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
					}
					globalFileUploads.setFileName(globalFileUploadVM.getMultipartFile().getOriginalFilename());
					globalFileUploads.setFilePath(destinationFile.getAbsolutePath()
							.split("ROOT")[destinationFile.getAbsolutePath().split("ROOT").length - 1]);
				}

				globalFileUploadsRepository.save(globalFileUploads);
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		return fileUploads;
	}

	@Override
	public Object updateTableData(CustomUserDetails customUserDetails) {
		String methodName = "uploadCompletionFile";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		HashMap<String, String> metaInfo = new HashMap<>();
		try {
			Date date = StringUtil.convertSimpleStringToDate("2022-12-01");

			Query query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false).and("production_date").gte(date));
			query.fields().include("production_date");
			query.fields().include("well");
			query.fields().include("chloride");

			List<Production> productionList = mongoOperations.find(query, Production.class);

			Map<Date, List<Production>> prodMap = productionList.stream()
					.collect(Collectors.groupingBy(Production::getProductionDate));

			query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false).and("reinjection_date").gte(date));
			query.fields().include("reinjection_date");
			query.fields().include("well");
			query.fields().include("chloride");

			List<Reinjection> reinjectionList = mongoOperations.find(query, Reinjection.class);

			Map<Date, List<Reinjection>> reinMap = reinjectionList.stream()
					.collect(Collectors.groupingBy(Reinjection::getReinjectionDate));

			query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false).and("date").gte(date));
			query.fields().include("date");
			query.fields().include("well");
			query.fields().include("utilities");

			List<DbDownTime> downList = mongoOperations.find(query, DbDownTime.class);

			Map<Date, List<DbDownTime>> downMap = downList.stream().collect(Collectors.groupingBy(DbDownTime::getDate));

			query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false).and("fiscalization_date").gte(date));

			List<OilLoadingandFiscalization> oilList = mongoOperations.find(query, OilLoadingandFiscalization.class);

			Map<Date, List<OilLoadingandFiscalization>> oilMap = oilList.stream()
					.collect(Collectors.groupingBy(OilLoadingandFiscalization::getFiscalizationDate));

			query = new Query();
			query.addCriteria(Criteria.where("delete_flag").is(false).and("date_time").gte(date));

			List<DbHeavyFuelOil> heavyList = mongoOperations.find(query, DbHeavyFuelOil.class);

			Map<Date, List<DbHeavyFuelOil>> heavyMap = heavyList.stream()
					.collect(Collectors.groupingBy(DbHeavyFuelOil::getDateTime));

			while (date.compareTo(StringUtil.convertSimpleStringToDate("2023-01-17")) <= 0) {
				List<DbUpdatesParameterDomain> uplodeList = new ArrayList<DbUpdatesParameterDomain>();
				if (prodMap.containsKey(date))
					for (Production prod : prodMap.get(date)) {
						if (StringUtil.haveValue(prod.getChloride())) {
							DbUpdatesParameterDomain dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain.setTable("production");
							dbUpdatesParameterDomain.setWell(prod.getWell());
							dbUpdatesParameterDomain.setDate(prod.getProductionDate());
							dbUpdatesParameterDomain.setChloride(prod.getChloride());
							uplodeList.add(dbUpdatesParameterDomain);
						}
					}
				if (reinMap.containsKey(date))
					for (Reinjection rein : reinMap.get(date)) {
						if (StringUtil.haveValue(rein.getChloride())) {
							DbUpdatesParameterDomain dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain.setTable("reinjection");
							dbUpdatesParameterDomain.setWell(rein.getWell());
							dbUpdatesParameterDomain.setDate(rein.getReinjectionDate());
							dbUpdatesParameterDomain.setChloride(rein.getChloride());
							uplodeList.add(dbUpdatesParameterDomain);
						}
					}
				if (downMap.containsKey(date))
					for (DbDownTime down : downMap.get(date)) {
						if (StringUtil.haveValue(down.getUtilities())) {
							DbUpdatesParameterDomain dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain.setTable("downtime");
							dbUpdatesParameterDomain.setWell(down.getWell());
							dbUpdatesParameterDomain.setDate(down.getDate());
							dbUpdatesParameterDomain.setUtilities(down.getUtilities());
							uplodeList.add(dbUpdatesParameterDomain);
						}
					}
				if (oilMap.containsKey(date))
					for (OilLoadingandFiscalization oil : oilMap.get(date)) {
						if (StringUtil.haveValue(oil.getOperationNumber())
								|| StringUtil.haveValue(oil.getFiscalizedTicekt())
								|| StringUtil.haveValue(oil.getContracto())
								|| StringUtil.haveValue(oil.getDilutedOilFiscalizedPerupetro())
								|| StringUtil.haveValue(oil.getDiluentFiscalizedPerupetro())
								|| StringUtil.haveValue(oil.getHeavyOilFiscalizedPerupetro())) {
							DbUpdatesParameterDomain dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain.setTable("oilLoadingandFiscalization");
							dbUpdatesParameterDomain.setWell(oil.getTank());
							dbUpdatesParameterDomain.setDate(oil.getFiscalizationDate());

							if (StringUtil.haveValue(oil.getOperationNumber()))
								dbUpdatesParameterDomain.setOperationNumber(oil.getOperationNumber());

							if (StringUtil.haveValue(oil.getFiscalizedTicekt()))
								dbUpdatesParameterDomain.setFiscalizedTicekt(oil.getFiscalizedTicekt());

							if (StringUtil.haveValue(oil.getContracto()))
								dbUpdatesParameterDomain.setContracto(oil.getContracto());

							if (StringUtil.haveValue(oil.getDilutedOilFiscalizedPerupetro()))
								dbUpdatesParameterDomain
										.setDilutedOilFiscalizedPerupetro(oil.getDilutedOilFiscalizedPerupetro());

							if (StringUtil.haveValue(oil.getDiluentFiscalizedPerupetro()))
								dbUpdatesParameterDomain
										.setDiluentFiscalizedPerupetro(oil.getDiluentFiscalizedPerupetro());

							if (StringUtil.haveValue(oil.getHeavyOilFiscalizedPerupetro()))
								dbUpdatesParameterDomain
										.setHeavyOilFiscalizedPerupetro(oil.getHeavyOilFiscalizedPerupetro());
							uplodeList.add(dbUpdatesParameterDomain);
						}
					}
				if (heavyMap.containsKey(date))
					for (DbHeavyFuelOil dbHeavyFuelOil : heavyMap.get(date)) {
						if (StringUtil.haveValue(dbHeavyFuelOil.getOperationNumber())
								|| StringUtil.haveValue(dbHeavyFuelOil.getFiscalizedTicket())
								|| StringUtil.haveValue(dbHeavyFuelOil.getComments())
								|| StringUtil.haveValue(dbHeavyFuelOil.getDiluentFiscalizedPerupetro())
								|| StringUtil.haveValue(dbHeavyFuelOil.getHeavyOilOilFiscalizedPerupetro())) {

							DbUpdatesParameterDomain dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain = new DbUpdatesParameterDomain();
							dbUpdatesParameterDomain.setTable("dbHeavyFuelOil");
							dbUpdatesParameterDomain.setWell(dbHeavyFuelOil.getTank());
							dbUpdatesParameterDomain.setDate(dbHeavyFuelOil.getDateTime());

							if (StringUtil.haveValue(dbHeavyFuelOil.getOperationNumber()))
								dbUpdatesParameterDomain.setOperationNumber(dbHeavyFuelOil.getOperationNumber());

							if (StringUtil.haveValue(dbHeavyFuelOil.getFiscalizedTicket()))
								dbUpdatesParameterDomain.setFiscalizedTicekt(dbHeavyFuelOil.getFiscalizedTicket());

							if (StringUtil.haveValue(dbHeavyFuelOil.getComments()))
								dbUpdatesParameterDomain.setContracto(dbHeavyFuelOil.getComments());

							if (StringUtil.haveValue(dbHeavyFuelOil.getDiluentFiscalizedPerupetro()))
								dbUpdatesParameterDomain
										.setDiluentFiscalizedPerupetro(dbHeavyFuelOil.getDiluentFiscalizedPerupetro());

							if (StringUtil.haveValue(dbHeavyFuelOil.getHeavyOilOilFiscalizedPerupetro()))
								dbUpdatesParameterDomain.setHeavyOilFiscalizedPerupetro(
										dbHeavyFuelOil.getHeavyOilOilFiscalizedPerupetro());

							uplodeList.add(dbUpdatesParameterDomain);
						}
					}
				mongoOperations.insertAll(uplodeList);
				date = StringUtil.addDays(date, 1);
			}

		} catch (

		CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode());
		}
		metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		return true;
	}

	@Override
	public Object createExcel(SearchVM searchVM, CustomUserDetails customUserDetails) {
		Map<String, Object> mapObj = new HashMap<String, Object>();

		try {
			// Define an ArrayList of Persons
			Production prod = new Production();
			ObjectMapper mapObject = new ObjectMapper();
			mapObj = mapObject.convertValue(prod, Map.class);

			FileWriter outputfile = new FileWriter("/home/kellhydlptp134/Desktop/test.csv");

			// create CSVWriter object filewriter object as parameter
			CSVWriter writer = new CSVWriter(outputfile);

			// adding header to csv
			String[] header = { "Name", "Class", "Marks" };
			writer.writeNext((String[]) mapObj.keySet().toArray());

			// add data to csv
			String[] data1 = { "Aman", "10", "620" };
			writer.writeNext((String[]) mapObj.values().toArray());
			String[] data2 = { "Suraj", "10", "630" };
			writer.writeNext(data2);

			// closing writer connection
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapObj;

	}

	@Override
	public Object updatePlantImport12Data(SearchVM searchVM, CustomUserDetails customUserDetails) {
		String methodName = "automaticProcess";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Date latestDate = null;
		List<GlobalFileUploads> fileUploads = null;

		try {

			Date startDate = StringUtil
					.setDateBegniningOfTheDayNew(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()));
			Date endDate = StringUtil.setDateEndOfTheDay(StringUtil.convertSimpleStringToDate(searchVM.getEndDate()));

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("category")
					.is("IMPORT 12. PLANT EVENTS").and("operation_date_time").gte(startDate).lte(endDate));
			fileUploads = mongoOperations.find(query, GlobalFileUploads.class);

			TreeMap<Date, List<GlobalFileUploads>> dayFiles = fileUploads.stream().collect(
					Collectors.groupingBy(GlobalFileUploads::getOperationDateTime, TreeMap::new, Collectors.toList()));

			for (Date date : dayFiles.keySet()) {
				latestDate = date;
				List<GlobalFileUploads> fileUploadsList = dayFiles.get(date);
				for (GlobalFileUploads file : fileUploadsList) {
					File destinationFile = new File(environment.getProperty("spring.history-uploads").split("ROOT")[0]
							+ "ROOT" + File.separator + file.getFilePath());
					if (!destinationFile.exists()) {
						throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
								CustomExceptionCode.FILE_DATA_NOT_FOUND.getErrMsg() + " " + file.getCategory() + " On "
										+ StringUtil.convertDateIntoYYMMdd(file.getOperationDateTime()),
								CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
					}

					FileInputStream input = new FileInputStream(destinationFile);
					MultipartFile multipartFile1 = new MockMultipartFile("file", destinationFile.getName(),
							"text/plain", IOUtils.toByteArray(input));

					automaticUploadFile(multipartFile1, file.getCategory(), file.getOperationDateTime(),
							customUserDetails);
				}
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public Object automaticProcessWithoutImport(SearchVM searchVM, CustomUserDetails customUserDetails) {
		String methodName = "automaticProcessWithoutImport";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Date latestDate = null;
		List<GlobalFileUploads> importDataAvailabilityCheckList = null;

		try {

			int totalcount = getGlobalImportDropdownList(customUserDetails).getTotalCount();

			Date startDate = StringUtil
					.setDateBegniningOfTheDayNew(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()));
			Date endDate = StringUtil.setDateEndOfTheDay(StringUtil.convertSimpleStringToDate(searchVM.getEndDate()));

			/*
			 * removeAboveDataOtherthanImportData(StringUtil.convertSimpleStringToDate(
			 * searchVM.getStartDate()), customUserDetails);
			 */

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lte(endDate));
			importDataAvailabilityCheckList = mongoOperations.find(query, GlobalFileUploads.class);
			TreeMap<Date, List<GlobalFileUploads>> dayFiles = importDataAvailabilityCheckList.stream().collect(
					Collectors.groupingBy(GlobalFileUploads::getOperationDateTime, TreeMap::new, Collectors.toList()));

			String dateString = "";
			String tableApproval = "";

			for (Date date : dayFiles.keySet()) {
				if (dayFiles.get(date).size() < totalcount) {
					if (dateString.length() > 0)
						dateString = dateString + ", " + StringUtil.convertDateIntoYYMMdd(date);
					else
						dateString = StringUtil.convertDateIntoYYMMdd(date);
				}
				String tableApprovalInside = "";
				for (GlobalFileUploads approvalCheck : dayFiles.get(date)) {
					if (!approvalCheck.isApproved()) {
						if (tableApprovalInside.length() > 0)
							tableApprovalInside = tableApprovalInside + ", " + approvalCheck.getCategory();
						else
							tableApprovalInside = StringUtil.convertDateIntoYYMMdd(date) + "[ "
									+ approvalCheck.getCategory();
					}
				}
				if (tableApprovalInside.length() > 0) {
					tableApprovalInside = tableApprovalInside + "]\n";
					tableApproval = tableApproval + tableApprovalInside;
				}
			}
			if (dateString.length() > 0 && tableApproval.length() > 0) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						"Required More Data to Process on the following dates " + dateString
								+ "\nData is not in approved state for the following tables on specific date "
								+ tableApproval,
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
			} else if (dateString.length() > 0) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						"Required More Data to Process on the following dates " + dateString,
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
			} else if (tableApproval.length() > 0) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						"Data is not in approved state for the following tables on specific date " + tableApproval,
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());
			}

			List<String> dateList = new ArrayList<String>();
			for (Date date : dayFiles.keySet()) {
				dateList.add(StringUtil.convertDateIntoYYMMdd(date));
				chronProcessedWithDateWithOutVfm(customUserDetails, date);
			}

			String content = "";
			if (StringUtil.haveElements(dateList) && dateList.size() == 1) {
				content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
						+ "<div style=\"width: 90%; margin: auto\">\n" + "	\n" + "	<div class=\"graphView\">\n"
						+ "		<h4> Daily document uploads successfully done for the following Date " + dateList.get(0)
						+ " by " + customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n"
						+ "</body>\n" + "</html>";

			} else {
				content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
						+ "<div style=\"width: 90%; margin: auto\">\n" + "	\n" + "	<div class=\"graphView\">\n"
						+ "		<h4> Group uploads successfully done for the following Dates " + dateList + " by "
						+ customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n" + "</body>\n"
						+ "</html>";
			}
			String subject = "Daily Document Upload";

			reportMailSend(customUserDetails, content, subject);

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public Object removeAboveDataOtherthanImportData(Date operationDateTime, CustomUserDetails customUserDetails) {
		String methodName = "removeAboveDataOtherthanImportData";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		ArrayList<String> collectionNames = new ArrayList<String>(Arrays.asList( "daily_hp_ubh",
				"daily_hps", "daily_reinjection", "daily_well_potential", "daily_total_well_potential_and_production",
				"dispatch_tanks", "dispatch_tanks_reported", "daily_total_tank_available_storage",
				"daily_totalmixtanklevel_fiscalization", "daily_totaldiluenttanklevel_fiscalization", "diluent_tanks",
				"daily_tank_total", "daily_crude_shipment", "daily_oil_for_generation",
				"daily_total_crude_shipment_and_generation", "daily_oil_balance", "consolidated_downtime"));
		// don't remove daily_well_test as it generated at the time of import6 upload
		try {

			Date startDate = StringUtil.setDateBegniningOfTheDayNew(operationDateTime);
			Date endDate = StringUtil.setDateBegniningOfTheDayNew(new Date());

			Query query = new Query();
			query.addCriteria(Criteria.where("operation_date_time").gte(startDate).lt(endDate));

			for (String name : collectionNames) {
				mongoOperations.remove(query, name);
			}

			query = new Query();
			query.addCriteria(Criteria.where("date_time").gte(startDate).lt(endDate));
			// mongoOperations.remove(query, "daily_well_parameters");
			// don't remove daily well parameters as it consists of import 10 well potential
			// data
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");
			mongoOperations.remove(query, "db_heavy_fuel_oil");

			query = new Query();
			query.addCriteria(Criteria.where("fecha").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_consolidated_diluent");
			mongoOperations.remove(query, "db_diluent");

			query = new Query();
			query.addCriteria(Criteria.where("intial_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_dilutent_barges");
			mongoOperations.remove(query, "db_oil_barges");

			query = new Query();
			query.addCriteria(Criteria.where("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_down_time");
			mongoOperations.remove(query, "db_laboratory");
			mongoOperations.remove(query, "daily_gas_balance");

			query = new Query();
			query.addCriteria(Criteria.where("mode").is("Generated").and("date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "report_files");

			query = new Query();
			query.addCriteria(Criteria.where("production_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_monthly_production");
			mongoOperations.remove(query, "db_production");

			query = new Query();
			query.addCriteria(Criteria.where("fiscalization_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			query = new Query();

			query.addCriteria(Criteria.where("end_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_oil_loading_and_fiscalization");
			mongoOperations.remove(query, "db_heavy_fuel_oil");
			mongoOperations.remove(query, "db_diluent_unloading_and_fiscaliza");

			query = new Query();
			query.addCriteria(Criteria.where("reinjection_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_reinjection");

			query = new Query();
			query.addCriteria(Criteria.where("summary_date").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "db_summary");

			query = new Query();
			query.addCriteria(Criteria.where("date_time").gte(startDate).lt(endDate));
			mongoOperations.remove(query, "real_time_solids_monitoring");
			mongoOperations.remove(query, "real_time_solids_monitoring_day_total");
			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE));
			ExtraTableChron chron = mongoOperations.findAndRemove(query, ExtraTableChron.class);
			List<Date> dates = new ArrayList<Date>();
			if (StringUtil.haveValue(chron)) {
				for (Date date : chron.getDates()) {
					if (date.before(startDate)) {
						dates.add(date);
					}
				}
				chron.setDates(dates);
				mongoOperations.save(chron);
			}

			query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lt(endDate));
			List<GlobalFileUploads> fileUploads = mongoOperations.find(query, GlobalFileUploads.class);

			for (GlobalFileUploads file : fileUploads) {
				file.setProcessed(false);
			}
			globalFileUploadsRepository.save(fileUploads);

			collectionNames = new ArrayList<String>(Arrays.asList( "daily_hp_ubh", "daily_hps",
					"daily_reinjection", "daily_well_potential", "daily_total_well_potential_and_production",
					"dispatch_tanks", "dispatch_tanks_reported", "daily_total_tank_available_storage",
					"daily_totaldiluenttanklevel_fiscalization", "diluent_tanks", "daily_tank_total",
					"daily_crude_shipment", "daily_oil_for_generation", "daily_total_crude_shipment_and_generation",
					"daily_oil_balance", "consolidated_downtime", "db_diluent_unloading_and_fiscaliza",
					"db_heavy_fuel_oil", "db_consolidated_diluent", "db_diluent", "db_dilutent_barges", "db_oil_barges",
					"db_down_time", "db_laboratory", "daily_gas_balance", "db_monthly_production", "db_production",
					"db_oil_loading_and_fiscalization", "db_heavy_fuel_oil", "db_reinjection", "db_summary",
					"global_file_uploads", "report_files", "real_time_solids_monitoring",
					"real_time_solids_monitoring_day_total", "esp_diagnostics_data"));

			String content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
					+ "<div style=\"width: 90%; margin: auto\">\n" + "	\n" + "	<div class=\"graphView\">\n"
					+ "		<h4> Daily Upload Data  successfully deleted from the above Date "
					+ StringUtil.convertDateIntoYYMMdd(operationDateTime) + " to current Date by "
					+ customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n" + "</body>\n"
					+ "</html>";
			String subject = "Daily Upload Data deleted successfully";

			reportMailSend(customUserDetails, content, subject);

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(
					RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return collectionNames;
	}

	@Override
	public Date getLatestApprovedDate(CustomUserDetails customUserDetails) {
		String methodName = "getLatestApprovedDate";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Date latestDate = null;
		try {
			GlobalFileUploads latestApprovedrecord = globalFileUploadsRepository
					.findOneByDeleteFlagOrderByOperationDateTimeDesc(false);
			if (!StringUtil.haveValue(latestApprovedrecord)) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrMsg(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());

			} else {
				latestDate = latestApprovedrecord.getOperationDateTime();
			}

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public GlobalImportDropdownList getGlobalImportDropdownList(CustomUserDetails customUserDetails) {
		String methodName = "getLatestApprovedDate";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		GlobalImportDropdownList dropdownList = new GlobalImportDropdownList();
		try {
			Query query = new Query();
			dropdownList = mongoOperations.findOne(query, GlobalImportDropdownList.class);
			if (!StringUtil.haveValue(dropdownList)) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrMsg(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());

			}
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return dropdownList;
	}

	public Object toAvoidDuplicatesImportFiles(SearchVM searchVM, CustomUserDetails customUserDetails)
			throws ParseException {
		List<GlobalFileUploads> fileUpload = null;
		String data = null;
		try {
			if (StringUtil.haveValue(searchVM)) {
				Date startDate = StringUtil
						.setDateBegniningOfTheDayNew(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()));
				Date endDate = StringUtil
						.setDateEndOfTheDay(StringUtil.convertSimpleStringToDate(searchVM.getEndDate()));
				Query query = new Query();
				query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("category")
						.is(searchVM.getCategory()).and("operation_date_time").gte(startDate).lte(endDate));
				query.fields().include("operation_date_time");
				query.fields().include("category");
				fileUpload = mongoOperations.find(query, GlobalFileUploads.class);
				if (fileUpload.size() != 0) {
					data = "Import has been already uploaded for the day";
				} else {
					data = "There is no import uploaded for the day";
				}
			}
		} catch (CustomParameterizedException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 
	 * @param searchVM
	 * @param customUserDetails
	 * @description this method is the supporting method for chronProcessedWithDate
	 *              , when your trying for single day process but still some later
	 *              dates files are in approve state it will process that dates as
	 *              well.
	 * @return
	 */

	public Object chronProcessedWithDateSupport(SearchVM searchVM, CustomUserDetails customUserDetails) {
		String methodName = "automaticProcessWithoutImport";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		Date latestDate = null;
		List<GlobalFileUploads> importDataAvailabilityCheckList = null;

		try {

			int totalcount = getGlobalImportDropdownList(customUserDetails).getTotalCount();

			Date startDate = StringUtil
					.setDateBegniningOfTheDayNew(StringUtil.convertSimpleStringToDate(searchVM.getStartDate()));
			Date endDate = StringUtil.setDateEndOfTheDay(StringUtil.convertSimpleStringToDate(searchVM.getEndDate()));

			/*
			 * removeAboveDataOtherthanImportData(StringUtil.convertSimpleStringToDate(
			 * searchVM.getStartDate()), customUserDetails);
			 */

			Query query = new Query();
			query.addCriteria(Criteria.where(Constants.DELETE_FLAG).is(Constants.FALSE).and("operation_date_time")
					.gte(startDate).lte(endDate));
			importDataAvailabilityCheckList = mongoOperations.find(query, GlobalFileUploads.class);
			TreeMap<Date, List<GlobalFileUploads>> dayFiles = importDataAvailabilityCheckList.stream().collect(
					Collectors.groupingBy(GlobalFileUploads::getOperationDateTime, TreeMap::new, Collectors.toList()));

			List<Date> processDateList = new ArrayList<Date>();

			for (Date date : dayFiles.keySet()) {
				boolean fileCount = true;
				boolean tableApproval = true;

				if (dayFiles.get(date).size() < totalcount) {
					fileCount = false;
				}
				if (fileCount) {
					for (GlobalFileUploads approvalCheck : dayFiles.get(date)) {
						if (!approvalCheck.isApproved()) {
							tableApproval = false;
							break;
						}
					}
				}

				if (fileCount && tableApproval) {
					processDateList.add(date);
				} else {
					break;
				}
			}

			List<String> dateList = new ArrayList<String>();
			for (Date date : processDateList) {
				dateList.add(StringUtil.convertDateIntoYYMMdd(date));
				chronProcessedWithDateWithOutVfm(customUserDetails, date);
			}

			String content = "";
			if (StringUtil.haveElements(dateList) && dateList.size() == 1) {
				content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
						+ "<div style=\"width: 90%; margin: auto\">\n" + "	\n" + "	<div class=\"graphView\">\n"
						+ "		<h4> Daily document uploads successfully done for the following Date " + dateList.get(0)
						+ " by " + customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n"
						+ "</body>\n" + "</html>";

			} else {
				content = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
						+ "<div style=\"width: 90%; margin: auto\">\n" + "	\n" + "	<div class=\"graphView\">\n"
						+ "		<h4> Group uploads successfully done for the following Dates " + dateList + " by "
						+ customUserDetails.getUsername() + ".</h4>\n" + "	</div>	\n" + "</div>\n" + "</body>\n"
						+ "</html>";
			}
			String subject = "Daily Document Upload";

			reportMailSend(customUserDetails, content, subject);

		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return latestDate;
	}

	@Override
	public Object getGlobalImportDropdownLists(CustomUserDetails customUserDetails, Date operationDateTime) {
		String methodName = "getLatestApprovedDate";
		log.info(this.getClass() + " " + methodName + " " + Constants.START);
		GlobalImportDropdownList dropdownList = new GlobalImportDropdownList();
		try {
			String dates = "2023-08-01 00:00:00.0";
			Date dates1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(dates);
			if(operationDateTime.before(dates1)) {
//			dropdownList	=globalImportDropdownListRepository.findByType("before");
			
			Query query = new Query();
			query.addCriteria(Criteria.where("type").is("before"));
			dropdownList = mongoOperations.findOne(query, GlobalImportDropdownList.class);
			}else {
//				dropdownList =	globalImportDropdownListRepository.findByType("after");
				Query query = new Query();
				query.addCriteria(Criteria.where("type").is("after"));
				dropdownList = mongoOperations.findOne(query, GlobalImportDropdownList.class);
			}
			if (!StringUtil.haveValue(dropdownList)) {
				throw new CustomParameterizedException(RegisteredException.DATA_NOT_AVAILABLE.getException(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrMsg(),
						CustomExceptionCode.DATA_NOT_AVAILABLE.getErrCode());

			}
		} catch (CustomParameterizedException e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw e;
		} catch (Exception e) {
			log.error(
					this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			throw new CustomParameterizedException(RegisteredException.IMPORTXL_FILES_DATA_EXCEPTION.getException(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrMsg(),
					CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_DATA.getErrCode());
		}
		log.info(this.getClass() + " " + methodName + " " + Constants.END);
		return dropdownList;
	}

//	Added By Deenesh on 14/05/2025
	@Override
	public Object getMechanicalIntegrityUploadedFiles(SearchVM dateRangeVM, Pageable pageable) {
		String methodName = "getMechanicalIntegrityUploadedFiles";
	    log.info(this.getClass() + " " + methodName + " " + Constants.START);

	    Page<GlobalFileUploads> globalFileUploads = null;

	    try {
	        if (StringUtil.haveValue(dateRangeVM.getCategoryList())) {
	            if (StringUtil.haveValue(dateRangeVM.getStartDate()) && StringUtil.haveValue(dateRangeVM.getEndDate())) {
	                globalFileUploads = globalFileUploadsRepository
	                        .findByCategoryInAndDeleteFlagAndInspectionDateBetweenOrderByIndexAsc(
	                                dateRangeVM.getCategoryList(), false,
	                                StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
	                                StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
	            } else {
	                globalFileUploads = globalFileUploadsRepository
	                        .findByCategoryInAndDeleteFlagOrderByIndexAsc(dateRangeVM.getCategoryList(), false, pageable);
	            }
	        } else if (StringUtil.haveValue(dateRangeVM.getCategory())) {
	            if (StringUtil.haveValue(dateRangeVM.getStartDate()) && StringUtil.haveValue(dateRangeVM.getEndDate())) {
	                globalFileUploads = globalFileUploadsRepository
	                        .findByCategoryAndDeleteFlagAndInspectionDateBetweenOrderByIndexAsc(
	                                dateRangeVM.getCategory(), false,
	                                StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
	                                StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
	            } else {
	                globalFileUploads = globalFileUploadsRepository
	                        .findByCategoryAndDeleteFlagOrderByIndexAsc(dateRangeVM.getCategory(), false, pageable);
	            }
	        } else if (StringUtil.haveValue(dateRangeVM.getStartDate()) && StringUtil.haveValue(dateRangeVM.getEndDate())) {
	            globalFileUploads = globalFileUploadsRepository
	                    .findByDeleteFlagAndInspectionDateBetweenOrderByIndexAsc(
	                            false,
	                            StringUtil.setStringToStartDay(dateRangeVM.getStartDate()),
	                            StringUtil.setStringToEndDay(dateRangeVM.getEndDate()), pageable);
	        } else {
	            globalFileUploads = globalFileUploadsRepository.findByDeleteFlagOrderByIndexAsc(false, pageable);
	        }
	    } catch (CustomParameterizedException e) {
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
	        throw e;
	    } catch (Exception e) {
	        log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
	        throw new CustomParameterizedException(
	                RegisteredException.GLOBAL_FILE_UPLOADS_FETCH_EXCEPTION.getException(),
	                CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrMsg(),
	                CustomExceptionCode.EXCEPTION_OCCURED_WHILE_FETCHING_GLOBAL_FILE_UPLOADS.getErrCode());
	    }

	    log.info(this.getClass() + " " + methodName + " " + Constants.END);
	    return globalFileUploads;
	}

//	Added By Deenesh on 14/05/2025
	@Override
	public CustomResponse MechanicalIntegrityFileUpload(MultipartFile multipartFile, String category,
			Date inspectionDate, String description, CustomUserDetails customUserDetails) {
			
			String methodName = "uploadCompletionFile";
			log.info(this.getClass() + " " + methodName + " " + Constants.START);
			HashMap<String, String> metaInfo = new HashMap<>();

			GlobalFileUploads fileUploads = null;
			CustomResponse customResponse = null;
			File destinationFile = null;
			Double index = 0.0;
			try {
			    File destinationFolder = new File(environment.getProperty("spring.history-uploads") + File.separator
			            + category + File.separator + StringUtil.convertDateIntoYYMMdd(inspectionDate));

			    if (!destinationFolder.exists()) {
			        destinationFolder.mkdirs();
			    }

			    destinationFile = new File(destinationFolder.getPath(), multipartFile.getOriginalFilename());

			    if (destinationFile.exists()) {
			        destinationFile.delete();
			    }

			    if (!multipartFile.isEmpty()) {
			        multipartFile.transferTo(destinationFile);

			        if (destinationFile.exists()) {
//			            Path path = Paths.get(destinationFile.getPath());
//			            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rw-r--r--"));
			        	Path path = destinationFile.toPath();
					    try {
					        if (Files.getFileStore(path).supportsFileAttributeView(PosixFileAttributeView.class)) {
					            // For Linux/macOS (POSIX-compliant systems)
					            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxrwxrwx"));
					        } else {
					            log.info("Skipping POSIX file permission settings on non-POSIX file systems (e.g., Windows).");
					        }
					    } catch (IOException e) {
					        log.error("Failed to set file permissions: " + e.getMessage(), e);
					    }
			        

			        fileUploads = new GlobalFileUploads();
			        fileUploads.setFileName(multipartFile.getOriginalFilename());
			        fileUploads.setCategory(category);
			        fileUploads.setOperationDateTime(inspectionDate);
			        fileUploads.setInspectionDate(inspectionDate);
			        fileUploads.setDescription(description);
			        fileUploads.setIndex(0.0);
			        
			        String rootPath = environment.getProperty("spring.history-uploads");
			        String relativePath = destinationFile.getAbsolutePath().replace(rootPath, "");
			        fileUploads.setFilePath(relativePath);

			        mongoTemplate.save(fileUploads);

			        customResponse = new CustomResponse(Boolean.TRUE,
			                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessCode(),
			                SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getSuccessMessage(),
			                fileUploads, metaInfo);
			    }
			    }
			} catch (CustomParameterizedException e) {
			    log.error(this.getClass() + " " + methodName + " " + Constants.EXCEPTION + " log[ " + e.getMessage() + " ]");
			    return new CustomResponse(Boolean.FALSE,
			            CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
			            CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
			            metaInfo);
			} catch (Exception e) {
			    e.printStackTrace();
			    return new CustomResponse(Boolean.FALSE,
			            CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrCode(),
			            CustomExceptionCode.EXCEPTION_OCCURED_WHILE_UPLOADING_THE_FILES_.getErrMsg(), e.getMessage(),
			            metaInfo);
			}

			metaInfo.put(Constants.SERVICE_NAME, SuccessCode.GLOBAL_FILE_UPLOADED_SUCCESSFULLY.getServiceName());
			log.info(this.getClass() + " " + methodName + " " + Constants.END);
			return customResponse;

		
	}


}
