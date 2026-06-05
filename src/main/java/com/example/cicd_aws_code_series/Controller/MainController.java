package com.example.cicd_aws_code_series.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class MainController {

    @GetMapping(value = "/", produces = "text/html; charset=UTF-8")
    public String index() {
        String now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <title>CI/CD Deploy</title>
                  <style>
                    body { margin: 0; display: flex; justify-content: center; align-items: center; height: 100vh; background: #f0f4f8; font-family: sans-serif; }
                    .card { background: white; border-radius: 12px; padding: 40px 60px; box-shadow: 0 4px 16px rgba(0,0,0,0.1); text-align: center; }
                    h1 { font-size: 2rem; color: #2d3748; margin-bottom: 8px; }
                    .badge { display: inline-block; background: #3182ce; color: white; border-radius: 20px; padding: 4px 16px; font-size: 0.9rem; margin-bottom: 20px; }
                    .time { color: #718096; font-size: 0.95rem; margin-bottom: 24px; }
                    .pipeline { display: flex; align-items: center; justify-content: center; gap: 8px; flex-wrap: wrap; margin-top: 16px; }
                    .step { background: #ebf4ff; color: #3182ce; border-radius: 8px; padding: 6px 14px; font-size: 0.85rem; font-weight: 600; }
                    .step.active { background: #3182ce; color: white; }
                    .arrow { color: #a0aec0; font-size: 1rem; }
                    .pipeline-group { display: flex; align-items: center; gap: 8px; border: 2px dashed #3182ce; border-radius: 10px; padding: 8px 12px; position: relative; }
                    .pipeline-label { position: absolute; top: -10px; left: 12px; background: white; padding: 0 6px; font-size: 0.75rem; color: #3182ce; font-weight: 600; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="badge">✓ 배포 완료 <span style="color:#ffd700;">★</span></div>
                    <h1>Step 2. GitHub 연동</h1> 
                    <p class="time">%s</p>
                    <div class="pipeline">
                      <span class="step">Local Git</span>
                      <span class="arrow">→</span>
                      <span class="step">GitHub</span>
                      <span class="arrow">→</span>
                      <div class="pipeline-group">
                        <span class="pipeline-label">CodePipeline</span>
                        <span class="step">CodeBuild</span>
                        <span class="arrow">→</span>
                        <span class="step">CodeDeploy</span>
                      </div>
                      <span class="arrow">→</span>
                      <span class="step active">EC2</span>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(now);
    }
}
