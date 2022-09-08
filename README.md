 
## Problema

Pretende-se desenvolver um sistema de comunicação em rede para grupos de trabalho. O sistema deve permitir comunicação de 1 para 1 ou de 1 para todos, seguindo a seguinte especificação:  

- O serviço de chat corre na porta TCP 1234  
-  Todas as mensagens entre cliente e servidor devem ser terminadas com um caracter '\n'.  
- Cada cliente deve escolher um "nome" (ou nickname) quando se liga:  
assim que estabelece conexão, envia uma string "<nome>\n".
As mensagens passam a ter um prefixo:    
º + \<mensagem>\n (significa que a mensagem é para todos)  
º -<nome_receptor> \<mensagem>\n (significa que a mensagem é apenas para o cliente <nome_receptor>)  
º O cliente deve receber a mensagem com o prefixo "-<nome_emissor>" ou "+", para saber se é só para ele (e quem enviou) ou para todos.  

- Quando um cliente se desliga, todos os clientes ligados devem receber uma mensagem "<nome> desligou" (podemos assumir que mensagens do servidor sem prefixo "+" ou "-" são mensagens do sistema e podemos simplesmente imprimi-las no ecrã)  

## Implementação

- O trabalho deve ser implementado em grupos constituídos por um máximo de 2 alunos e pode ser implementado em qualquer linguagem de programação à escolha do grupo. Podem, inclusive, implementar o cliente e o servidor em linguagens diferentes;
- Deverão implementar um cliente interactivo e um servidor que suporte muitos clientes ligados ao mesmo tempo;
- O cliente deve receber mensagens do teclado e enviá-las para o servidor (no formato correcto), ao mesmo tempo que recebe mensagens do servidor e as mostra no ecrã (sugestão: usar select() entre o socket e o stdin);
- O servidor gere uma "tabela" onde faz corresponder cada "nome" ao socket do cliente respectivo (assumimos, para já, que não há nomes repetidos);
- O servidor deve ignorar mensagens que não obedeçam ao formato especificado.

## Entrega

O trabalho, realizado em grupos de 2 ou 1 elementos, deve ser entregue até às 23:59h do dia 26/04/2022. A entrega deve ser realizada através do moodle, pelo aluno com o menor dos números de aluno do grupo, consistindo num ficheiro comprimido (.zip ou .tar.gz) contendo uma directoria src/ com o código-fonte do trabalho e um ficheiro .pdf com o relatório de implementação.


---
