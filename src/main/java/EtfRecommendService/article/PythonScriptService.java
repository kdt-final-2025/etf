package EtfRecommendService.article;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class PythonScriptService {

    private static final Logger logger = LoggerFactory.getLogger(PythonScriptService.class);
    private static final String SCRIPTS_DIR = "scripts";
    private static final String SCRIPT_NAME = "python_executor.sh";

    /**
     * 파이썬 스크립트를 쉘 스크립트를 통해 실행합니다
     *
     * @param pythonFileName 실행할 파이썬 파일 이름 (scripts 디렉토리 내에 있는 파일명)
     * @return 실행 성공 여부
     */
    public boolean refreshNewsArticles(String pythonFileName) {
        try {
            // 현재 작업 디렉토리 (JAR가 실행되는 디렉토리)
            File currentDir = new File(".");
            String absoluteCurrentDir = currentDir.getAbsolutePath();

            // 스크립트 디렉토리의 절대 경로
            File scriptsDir = new File(currentDir, SCRIPTS_DIR);

            if (!scriptsDir.exists() || !scriptsDir.isDirectory()) {
                logger.error("스크립트 디렉토리를 찾을 수 없습니다: {}", scriptsDir.getAbsolutePath());
                return false;
            }

            // 실행할 쉘 스크립트의 절대 경로
            File scriptFile = new File(scriptsDir, SCRIPT_NAME);

            if (!scriptFile.exists() || !scriptFile.canExecute()) {
                logger.error("실행 가능한 스크립트를 찾을 수 없습니다: {}", scriptFile.getAbsolutePath());
                return false;
            }

            // 파이썬 파일의 경로 (scripts/ 디렉토리 기준 상대 경로)
            String pythonFilePath = pythonFileName;

            logger.info("작업 디렉토리: {}", absoluteCurrentDir);
            logger.info("스크립트 경로: {}", scriptFile.getAbsolutePath());
            logger.info("파이썬 파일: {}", pythonFilePath);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "bash", scriptFile.getAbsolutePath(), "-f", pythonFilePath);

            // 작업 디렉토리를 scripts/ 디렉토리로 설정
            processBuilder.directory(scriptsDir);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("스크립트 출력: {}", line);
                }
            }

            int exitCode = process.waitFor();
            logger.info("스크립트 실행 완료, 종료 코드: {}", exitCode);

            return exitCode == 0;
        } catch (Exception e) {
            logger.error("스크립트 실행 오류: {}", e.getMessage(), e);
            return false;
        }
    }
}